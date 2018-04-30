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

import io.scif.img.IO;
import io.scif.img.ImgIOException;
import io.scif.img.SCIFIOImgPlus;

import java.util.List;
import java.util.Random;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.algorithm.neighborhood.DiamondShape;
import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class DilationTests
{
	public static void main( final String[] args ) throws ImgIOException
	{
		show( args );
//		benchmark( args );
//		chain( args );
	}

	public static void chain( final String[] args )
	{
		ImageJ.main( args );

		final List< Shape > strel = StructuringElements.disk( 6, 2, 4 );
		for ( final Shape shape : strel )
		{
			System.out.println( shape );
			System.out.println( MorphologyUtils.printNeighborhood( shape, 2 ) );

		}
		final FloatType minVal = new FloatType( 0f );

		final ArrayImg< FloatType, FloatArray > img = ArrayImgs.floats( new long[] { 200, 200 } );
		final ArrayImg< BitType, LongArray > bitsImg = ArrayImgs.bits( new long[] { img.dimension( 0 ), img.dimension( 1 ) } );
		final ArrayRandomAccess< FloatType > ra = img.randomAccess();
		final ArrayRandomAccess< BitType > raBits = bitsImg.randomAccess(); // LOL
		final Random ran = new Random( 1l );
		for ( int i = 0; i < 100; i++ )
		{
			final int x = ran.nextInt( ( int ) img.dimension( 0 ) );
			final int y = ran.nextInt( ( int ) img.dimension( 1 ) );
			ra.setPosition( new int[] { x, y } );
			ra.get().set( 255f );
			raBits.setPosition( new int[] { x, y } );
			raBits.get().set( true );
		}
		ImageJFunctions.show( img, "Source" );

//		// Dilate to provided target
//		final Interval interval2 = FinalInterval.createMinSize( new long[] { 50, 50, 85, 50 } );
//		final Img< FloatType > img2 = img.factory().create( interval2, new FloatType() );
//		final long[] translation = new long[ interval2.numDimensions() ];
//		interval2.min( translation );
//		final IntervalView< FloatType > translate = Views.translate( img2, translation );
//		Dilation.dilate( img, translate, strel, minVal, 1 );
//		ImageJFunctions.show( img2, "DilatedToTarget" );

//		// Dilate to new image
//		final Img< FloatType > img3 = Dilation.dilate( img, strel, minVal, 1 );
//		ImageJFunctions.show( img3, "DilatedToNewImg" );

		// Dilate to new image FULL version.
		final Img< FloatType > img4 = Dilation.dilateFull( img, strel, minVal, 1 );
		ImageJFunctions.show( img4, "DilatedToNewImgFULL" );

//		// Dilate in place
//		final Interval interval = FinalInterval.createMinSize( new long[] { 100, -10, 80, 100 } );
//		Dilation.dilateInPlace( img, interval, strel, minVal, 1 );
//		ImageJFunctions.show( img, "DilatedInPlace" );
//
//		ImageJFunctions.show( img, "SourceAgain" );
//
//		/*
//		 * Binary type
//		 */
//
//		ImageJFunctions.show( bitsImg, "BitsSource" );
//
//		// Dilate to new image
//		final Img< BitType > imgBits3 = Dilation.dilate( bitsImg, strel, 1 );
//		ImageJFunctions.show( imgBits3, "BitsDilatedToNewImg" );
	}

	public static void show( final String[] args ) throws ImgIOException
	{
		ImageJ.main( args );
		final Shape strel = new DiamondShape( 3 );

		final String fn = "DrosophilaWing.tif";
		final List< SCIFIOImgPlus< FloatType > > imgs = IO.openImgs( fn, new ArrayImgFactory<>( new FloatType() ) );
		final Img< FloatType > img = imgs.get( 0 ).getImg();

//		final ArrayImg< FloatType, FloatArray > img = ArrayImgs.floats( new long[] { 800, 600 } );
//		final ArrayRandomAccess< FloatType > ra = img.randomAccess();
//		final Random ran = new Random( 1l );
//		for ( int i = 0; i < 100; i++ )
//		{
//			final int x = ran.nextInt( ( int ) img.dimension( 0 ) );
//			final int y = ran.nextInt( ( int ) img.dimension( 1 ) );
//			ra.setPosition( new int[] { x, y } );
//			ra.get().set( 255f );
//		}

		ImageJFunctions.show( img, "Source" );

		// Dilate to provided target
		final Interval interval2 = FinalInterval.createMinSize( new long[] { 280, 200, 185, 100 } );
		final Img< FloatType > img2 = img.factory().create( interval2 );
		final long[] translation = new long[ interval2.numDimensions() ];
		interval2.min( translation );
		final IntervalView< FloatType > translate = Views.translate( img2, translation );
		Dilation.dilate( img, translate, strel, 1 );
		ImageJFunctions.show( img2, "DilatedToTarget" );

		// Dilate to new image
		final Img< FloatType > img3 = Dilation.dilate( img, strel, 1 );
		ImageJFunctions.show( img3, "DilatedToNewImg" );

		// Dilate to new image FULL version.
		final Img< FloatType > img4 = Dilation.dilateFull( img, strel, 1 );
		ImageJFunctions.show( img4, "DilatedToNewImgFULL" );

		// Dilate in place
		final Interval interval = FinalInterval.createMinSize( new long[] { 100, -10, 200, 200 } );
		Dilation.dilateInPlace( img, interval, strel, 1 );
		ImageJFunctions.show( img, "DilatedInPlace" );

		/*
		 * Binary type
		 */

		final ArrayImg< BitType, LongArray > bitsImg = ArrayImgs.bits( new long[] { 800, 600 } );

		final ArrayRandomAccess< BitType > raBits = bitsImg.randomAccess(); // LOL
		final Random ran2 = new Random( 1l );
		for ( int i = 0; i < 100; i++ )
		{
			final int x = ran2.nextInt( ( int ) bitsImg.dimension( 0 ) );
			final int y = ran2.nextInt( ( int ) bitsImg.dimension( 1 ) );
			raBits.setPosition( new int[] { x, y } );
			raBits.get().set( true );
		}
		ImageJFunctions.show( bitsImg, "BitsSource" );

		// Dilate to new image
		final Img< BitType > imgBits3 = Dilation.dilate( bitsImg, strel, 1 );
		ImageJFunctions.show( imgBits3, "BitsDilatedToNewImg" );

	}

	public static final void benchmark( final String[] args )
	{
		final int ntrials = 10;

		final Shape strel = new DiamondShape( 3 );

		final ArrayImg< FloatType, FloatArray > img = ArrayImgs.floats( new long[] { 200, 200 } );
		final ArrayRandomAccess< FloatType > ra = img.randomAccess();

		final ArrayImg< BitType, LongArray > bitsImg = ArrayImgs.bits( new long[] { img.dimension( 0 ), img.dimension( 1 ) } );
		final ArrayRandomAccess< BitType > raBits = bitsImg.randomAccess(); // LOL

		final Random ran = new Random( 1l );
		for ( int i = 0; i < 1000; i++ )
		{
			final int x = ran.nextInt( ( int ) img.dimension( 0 ) );
			final int y = ran.nextInt( ( int ) img.dimension( 1 ) );

			ra.setPosition( new int[] { x, y } );
			ra.get().set( 1f );

			raBits.setPosition( ra );
			raBits.get().set( true );
		}

		// Dilate to new image
		Img< BitType > imgBits3 = Dilation.dilate( bitsImg, strel, 1 );
		long start = System.currentTimeMillis();
		for ( int i = 0; i < ntrials; i++ )
		{
			imgBits3 = Dilation.dilate( bitsImg, strel, 1 );
		}
		long end = System.currentTimeMillis();
		System.out.println( "BitType time: " + ( ( end - start ) / ntrials ) + " ms." );

		Img< FloatType > img3 = Dilation.dilate( img, strel, 1 );
		start = System.currentTimeMillis();
		for ( int i = 0; i < ntrials; i++ )
		{
			img3 = Dilation.dilate( img, strel, 1 );
		}
		end = System.currentTimeMillis();
		System.out.println( "FloatType time: " + ( ( end - start ) / ntrials ) + " ms." );

		ImageJ.main( args );
		ImageJFunctions.show( img3, "Float" );
		ImageJFunctions.show( imgBits3, "Bit" );
	}
}
