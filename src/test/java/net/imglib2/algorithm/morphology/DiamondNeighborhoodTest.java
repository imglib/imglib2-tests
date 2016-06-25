/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
package net.imglib2.algorithm.morphology;


import ij.ImageJ;

import java.util.List;

import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;


public class DiamondNeighborhoodTest
{

	public static void main( final String[] args )
	{
		main0( args ); // Look at strels
		main1( args ); // Benchmark 3D case
		main3( args ); // Benchmark 2D case
		main7( args ); // Print the decomposition of a 3D strel
	}

	public static void main0( final String[] args )
	{
		ImageJ.main( args );
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 19, 19, 19 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new long[] { 9, 9, 9 } );
		ra.get().set( 255 );

		final int[] radiuses = new int[] { 2, 3, 5 };
		for ( final int radius : radiuses )
		{
			final List< Shape > strelStd = StructuringElements.diamond( radius, img.numDimensions(), false );
			final Img< UnsignedByteType > std = Dilation.dilate( img, strelStd, 1 );
			final List< Shape > strelOpt = StructuringElements.diamond( radius, img.numDimensions(), true );
			final Img< UnsignedByteType > opt = Dilation.dilate( img, strelOpt, 1 );

			ImageJFunctions.show( std, "Std " + radius );
			ImageJFunctions.show( opt, "Opt " + radius );
		}
	}

	/**
	 * Performance comparison between optimized & standard strel, 3D case.
	 */
	public static void main1( final String[] args )
	{
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 39, 39, 39 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new long[] { 19, 19, 19 } );
		ra.get().set( 255 );

		System.out.println( "Full strel" );
		System.out.println( "radius\ttime(ms)" );
		// Warm up
		Dilation.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), false ), 1 );
		Dilation.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), false ), 1 );
		for ( int i = 1; i < 19; i++ )
		{
			final long start = System.currentTimeMillis();
			final List< Shape > strels = StructuringElements.diamond( i, img.numDimensions(), false );
			Dilation.dilate( img, strels, 1 );
			final long end = System.currentTimeMillis();
			System.out.println( "" + i + '\t' + ( end - start ) );
		}

		System.out.println( "Decomposed strel" );
		System.out.println( "radius\ttime(ms)" );
		// Warm up
		Dilation.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), true ), 1 );
		Dilation.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), true ), 1 );
		for ( int i = 1; i < 19; i++ )
		{
			final long start = System.currentTimeMillis();
			final List< Shape > strels = StructuringElements.diamond( i, img.numDimensions(), true );
			Dilation.dilate( img, strels, 1 );
			final long end = System.currentTimeMillis();
			System.out.println( "" + i + '\t' + ( end - start ) );
		}
	}

	/**
	 * Tune optimized 3D cases
	 *
	 * @param args
	 */
	public static void main7( final String[] args )
	{
		ImageJ.main( args );

		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 19, 19, 19 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new long[] { 9, 9, 9 } );
		ra.get().set( 255 );

		final long start = System.currentTimeMillis();
		final List< Shape > strels = StructuringElements.diamond( 6, img.numDimensions(), true );

		Img< UnsignedByteType > dilated = img;
		for ( final Shape strel : strels )
		{
			final String str = MorphologyUtils.printNeighborhood( strel, img.numDimensions() );
			System.out.println( str );
			dilated = Dilation.dilate( dilated, strel, 1 );
			ImageJFunctions.show( dilated, "Decomposed strel: " + strel );
		}

		final long end = System.currentTimeMillis();
		System.out.println( "Optimized processing time: " + ( end - start ) + " ms." );


	}

	/**
	 * Performance comparison between optimized & standard strel, 2D case.
	 */
	public static void main3( final String[] args )
	{
		ImageJ.main( args );

		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 100, 100 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new long[] { 49, 49 } );
		ra.get().set( 255 );

		System.out.println( "Full strel" );
		System.out.println( "radius\ttime(ms)" );
		// Warm up
		Dilation.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), false ), 1 );
		Dilation.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), false ), 1 );
		for ( int i = 0; i < 40; i++ )
		{
			final long start = System.currentTimeMillis();
			final List< Shape > strels = StructuringElements.diamond( i, img.numDimensions(), false );
			Dilation.dilate( img, strels, 1 );
			final long end = System.currentTimeMillis();
			System.out.println( "" + i + '\t' + ( end - start ) );
		}

		System.out.println( "Decomposed strel" );
		System.out.println( "radius\ttime(ms)" );
		// Warm up
		Dilation.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), true ), 1 );
		Dilation.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), true ), 1 );
		for ( int i = 0; i < 40; i++ )
		{
			final long start = System.currentTimeMillis();
			final List< Shape > strels = StructuringElements.diamond( i, img.numDimensions(), true );
			Dilation.dilate( img, strels, 1 );
			final long end = System.currentTimeMillis();
			System.out.println( "" + i + '\t' + ( end - start ) );
		}
	}
}
