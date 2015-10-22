/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.nearestneighbor;

import ij.CompositeImage;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.KDTree;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.imageplus.ImagePlusImg;
import net.imglib2.img.imageplus.ImagePlusImgFactory;
import net.imglib2.neighborsearch.KNearestNeighborSearch;
import net.imglib2.neighborsearch.NearestNeighborSearch;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import tests.Timer;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class KNearestNeighborSearchPhyllotaxisBehavior
{
	final static private double pi2 = Math.PI * 2;
	final static private double goldenAngle = ( 3 - Math.sqrt( 5 ) ) * Math.PI;
	
	final static private Random rnd = new Random( 1234 );
	
	final static private long[] size = new long[]{ 1024, 768 };
	
	final static private int m = 10000;

	final static private double rotateByGoldenAngle( double phi )
	{
		phi += goldenAngle;
		if ( phi > Math.PI )
			phi -= pi2;
		return phi;
	}
	
	final static private void createPhyllotaxis1(
			final int[] samples,
			final double[][] coordinates,
			final double tx,
			final double ty,
			final double dr )
	{
		final int n = samples.length;
		double phi = 0;
		double r = 0;
		int sample = 0;
		for ( int i = 0; i < n; ++i )
		{
			final double cos = Math.cos( phi ) * r + tx;
			final double sin = Math.sin( phi ) * r + ty;
			
			sample = Math.max( 0, Math.min( 65535, Math.round( i ) ) );
			
			samples[ i ] = sample;
			coordinates[ i ][ 0 ] = cos;
			coordinates[ i ][ 1 ] = sin;
			
			r += dr;
			phi = rotateByGoldenAngle( phi );
		}
	}
	
	
	final static private void createPhyllotaxis2(
			final int[] samples,
			final double[][] coordinates,
			final double tx,
			final double ty,
			final double dr )
	{
		final int n = samples.length;
		double phi = 0;
		double r = 0;
		int sample = 0;
		for ( int i = 0; i < n; ++i )
		{
			final double cos = Math.cos( phi ) * r + tx;
			final double sin = Math.sin( phi ) * r + ty;
			
			sample = Math.max( 0, Math.min( 65535, Math.round( i ) ) );
			
			samples[ i ] = sample;
			coordinates[ i ][ 0 ] = cos;
			coordinates[ i ][ 1 ] = sin;
			
			r += dr / ( r + 1 );
			phi = rotateByGoldenAngle( phi );
		}
	}
	
	
	final static private void createPhyllotaxis2(
			final RealPointSampleList< UnsignedShortType > phyllotaxis,
			final int n,
			final double tx,
			final double ty,
			final double dr )
	{
		double phi = 0;
		double r = 0;
		int phyllotaxisSample = 0;
		int f1 = 0;
		int f2 = 0;
		for ( int i = 0; i < n; ++i )
		{
			final double cos = Math.cos( phi ) * r + tx;
			final double sin = Math.sin( phi ) * r + ty;
			
			phyllotaxisSample = Math.max( 0, Math.min( 65535, Math.round( i ) ) );
			phyllotaxis.add( new RealPoint( new double[]{ cos, sin, 0 } ), new UnsignedShortType( phyllotaxisSample ) );
			
			final int fibonacciSample;
			
			if ( i == 1 )
			{
				f1 = 1;
				f2 = 2;
				fibonacciSample = 1;
			}
			else if ( i == f2 )
			{
				/* exchange f1 and f2 */
				f1 = f1 ^ f2;
				f2 = f1 ^ f2;
				f1 = f1 ^ f2;
				
				/* sum */
				f2 += i;
				
				fibonacciSample = 1;
			}
			else
				fibonacciSample = 0;
			
			phyllotaxis.add( new RealPoint( new double[]{ cos, sin, 1 } ), new UnsignedShortType( fibonacciSample ) );
			
			
			r += dr / ( r + 1 );
			phi = rotateByGoldenAngle( phi );
		}
	}
		
	private KNearestNeighborSearchPhyllotaxisBehavior(){}
	
	final static private < T extends Type< T > > long drawNearestNeighbor(
			final IterableInterval< T > target,
			final NearestNeighborSearch< T > nnSearch )
	{
		final Timer timer = new Timer();
		timer.start();
		final Cursor< T > c = target.localizingCursor();
		while ( c.hasNext() )
		{
			c.fwd();
			nnSearch.search( c );
			c.get().set( nnSearch.getSampler().get() );
		}
		return timer.stop();
	}
	
	
	final static private < T extends RealType< T > > long drawWeightedByDistance(
			final IterableInterval< T > target,
			final KNearestNeighborSearch< T > knnSearch,
			final int k,
			final double p,
			final double min,
			final double max )
	{
		final Timer timer = new Timer();
		timer.start();
		final Cursor< T > c = target.localizingCursor();
		while ( c.hasNext() )
		{
			c.fwd();
			knnSearch.search( c );
			double s = 0;
			double v = 0;
			for ( int i = 0; i < k; ++i )
			{
				final double d = knnSearch.getSquareDistance( i );
				if ( d > 0.001 )
				{
					final double w = 1.0 / Math.pow(  d, p );
					v += w * knnSearch.getSampler( i ).get().getRealDouble();
					s += w;
				}
				else
				{
					s = 1.0;
					v = knnSearch.getSampler( i ).get().getRealDouble();
				}
			}
			v /= s;
			
			c.get().setReal( Math.max(  min, Math.min( max, v ) ) );
		}
		return timer.stop();
	}
	
	
	
	final static public void main( final String[] args )
	{
		long t;

//		final int[] samples = new int[ m ];
//		final double[][] coordinates = new double[ m ][ 2 ];
		
//		createPhyllotaxis1( samples, coordinates, size[ 0 ] / 2.0, size[ 1 ] / 2.0, 0.1 );
//		createPattern2( samples, coordinates, size[ 0 ] / 2.0, size[ 1 ] / 2.0, 20 );
		
//		final RealPointSampleList< UnsignedShortType > list = new RealPointSampleList< UnsignedShortType >( 2 );
//		for ( int i = 0; i < samples.length; ++i )
//			list.add( new RealPoint( coordinates[ i ] ), new UnsignedShortType( samples[ i ] ) );

		final RealPointSampleList< UnsignedShortType > list = new RealPointSampleList< UnsignedShortType >( 3 );

		createPhyllotaxis2( list, m, size[ 0 ] / 2.0, size[ 1 ] / 2.0, 20 );

		final ImagePlusImgFactory< UnsignedShortType > factory = new ImagePlusImgFactory< UnsignedShortType >();

		final KDTree< UnsignedShortType > kdtree = new KDTree< UnsignedShortType >( list );

		new ImageJ();

		
		IJ.log( "KDTree Search" );
		IJ.log( "=============" );
		
		/* nearest neighbor */
		IJ.log( "Nearest neighbor ..." );
		final ImagePlusImg< UnsignedShortType, ? > img4 = factory.create( new long[]{ size[ 0 ], size[ 1 ], 2 }, new UnsignedShortType() );
		t = drawNearestNeighbor(
				img4,
				new NearestNeighborSearchOnKDTree< UnsignedShortType >( kdtree ) );
		
		IJ.log( t + "ms " );
		
		try
		{
			final ImagePlus imp4 = img4.getImagePlus();
			imp4.setOpenAsHyperStack( true );
			final CompositeImage impComposite = new CompositeImage( imp4, CompositeImage.COMPOSITE );
			impComposite.show();
			impComposite.setSlice( 1 );
			IJ.run( impComposite, "Grays", "" );
			impComposite.setDisplayRange( 0, m - 1 );
			impComposite.setSlice( 2 );
			impComposite.setDisplayRange( 0, 2 );
			impComposite.updateAndDraw();
			IJ.log( "Done." );
		}
		catch ( final ImgLibException e )
		{
			IJ.log( "Didn't work out." );
			e.printStackTrace();
		}
	}
}
