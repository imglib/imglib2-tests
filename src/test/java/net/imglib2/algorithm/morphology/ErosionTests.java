package net.imglib2.algorithm.morphology;

import ij.ImageJ;
import io.scif.img.ImgIOException;

import java.util.List;
import java.util.Random;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.algorithm.morphology.neighborhoods.DiamondShape;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class ErosionTests
{
	public static void main( final String[] args ) throws ImgIOException
	{
//		show( args );
		benchmark( args );
		chain( args );
	}

	private static void chain( final String[] args )
	{
		ImageJ.main( args );

		final List< Shape > strel = StructuringElements.disk( 6, 2, 4 );
		for ( final Shape shape : strel )
		{
			System.out.println( shape );
			System.out.println( MorphologyUtils.printNeighborhood( shape, 2 ) );
		}
		final FloatType maxVal = new FloatType( Float.POSITIVE_INFINITY );

		final ArrayImg< FloatType, FloatArray > img = ArrayImgs.floats( new long[] { 200, 200 } );
		for ( final FloatType pixel : img )
		{
			pixel.set( 1f );
		}

		final ArrayImg< BitType, LongArray > bitsImg = ArrayImgs.bits( new long[] { img.dimension( 0 ), img.dimension( 1 ) } );
		for ( final BitType pixelB : bitsImg )
		{
			pixelB.set( true );
		}

		final ArrayRandomAccess< FloatType > ra = img.randomAccess();
		final ArrayRandomAccess< BitType > raBits = bitsImg.randomAccess(); // LOL
		final Random ran = new Random( 1l );
		for ( int i = 0; i < 100; i++ )
		{
			final int x = ran.nextInt( ( int ) img.dimension( 0 ) );
			final int y = ran.nextInt( ( int ) img.dimension( 1 ) );
			ra.setPosition( new int[] { x, y } );
			ra.get().set( 0f );
			raBits.setPosition( new int[] { x, y } );
			raBits.get().set( false );
		}
		ImageJFunctions.show( img, "Source" );

		// Dilate to provided target
		final Interval interval2 = FinalInterval.createMinSize( new long[] { 50, 50, 85, 50 } );
		final Img< FloatType > img2 = img.factory().create( interval2, new FloatType() );
		final long[] translation = new long[ interval2.numDimensions() ];
		interval2.min( translation );
		final IntervalView< FloatType > translate = Views.translate( img2, translation );
		Erosion.erode( img, translate, strel, maxVal, 1 );
		ImageJFunctions.show( img2, "ErodedToTarget" );

		// Dilate to new image
		final Img< FloatType > img3 = Erosion.erode( img, strel, maxVal, 1 );
		ImageJFunctions.show( img3, "ErodedToNewImg" );

		// Dilate to new image FULL version.
		final Img< FloatType > img4 = Erosion.erodeFull( img, strel, maxVal, 1 );
		ImageJFunctions.show( img4, "ErodedToNewImgFULL" );

		// Dilate in place
		final Interval interval = FinalInterval.createMinSize( new long[] { 100, -10, 80, 100 } );
		Erosion.erodeInPlace( img, interval, strel, maxVal, 1 );
		ImageJFunctions.show( img, "ErodedInPlace" );

		ImageJFunctions.show( img, "SourceAgain" );

		/*
		 * Binary type
		 */

		ImageJFunctions.show( bitsImg, "BitsSource" );

		// Dilate to new image
		final Img< BitType > imgBits3 = Erosion.erode( bitsImg, strel, 1 );
		ImageJFunctions.show( imgBits3, "BitsErodedToNewImg" );
	}

	public static void show( final String[] args ) throws ImgIOException
	{
		ImageJ.main( args );
		final Shape strel = new DiamondShape( 3 );
		final FloatType maxVal = new FloatType( Float.POSITIVE_INFINITY );

//		final String fn = "DrosophilaWing.tif";
//		final List< SCIFIOImgPlus< FloatType >> imgs = new
//				ImgOpener().openImgs( fn, new ArrayImgFactory< FloatType >(), new
//						FloatType() );
//		final Img< FloatType > img = imgs.get( 0 ).getImg();

		final ArrayImg< FloatType, FloatArray > img = ArrayImgs.floats( new long[] { 800, 600 } );
		for ( final FloatType pixel : img )
		{
			pixel.set( 255f );
		}
		final ArrayRandomAccess< FloatType > ra = img.randomAccess();
		final Random ran = new Random( 1l );
		for ( int i = 0; i < 100; i++ )
		{
			final int x = ran.nextInt( ( int ) img.dimension( 0 ) );
			final int y = ran.nextInt( ( int ) img.dimension( 1 ) );
			ra.setPosition( new int[] { x, y } );
			ra.get().set( 0f );
		}

		ImageJFunctions.show( img, "Source" );

		// Dilate to provided target
		final Interval interval2 = FinalInterval.createMinSize( new long[] { 280, 200, 185, 100 } );
		final Img< FloatType > img2 = img.factory().create( interval2, new FloatType() );
		final long[] translation = new long[ interval2.numDimensions() ];
		interval2.min( translation );
		final IntervalView< FloatType > translate = Views.translate( img2, translation );
		Erosion.erode( img, translate, strel, maxVal, 1 );
		ImageJFunctions.show( img2, "ErodedToTarget" );

		// Dilate to new image
		final Img< FloatType > img3 = Erosion.erode( img, strel, maxVal, 1 );
		ImageJFunctions.show( img3, "ErodedToNewImg" );

		// Dilate to new image FULL version.
		final Img< FloatType > img4 = Erosion.erodeFull( img, strel, maxVal, 1 );
		ImageJFunctions.show( img4, "ErodedToNewImgFULL" );

		// Dilate in place
		final Interval interval = FinalInterval.createMinSize( new long[] { 100, -10, 200, 200 } );
		Erosion.erodeInPlace( img, interval, strel, maxVal, 1 );
		ImageJFunctions.show( img, "ErodedInPlace" );

		ImageJFunctions.show( img, "SourceAgain" );

		/*
		 * Binary type
		 */

		final ArrayImg< BitType, LongArray > bitsImg = ArrayImgs.bits( new long[] { 800, 600 } );
		for ( final BitType bitType : bitsImg )
		{
			bitType.set( true );
		}

		final ArrayRandomAccess< BitType > raBits = bitsImg.randomAccess(); // LOL
		final Random ran2 = new Random( 1l );
		for ( int i = 0; i < 100; i++ )
		{
			final int x = ran2.nextInt( ( int ) bitsImg.dimension( 0 ) );
			final int y = ran2.nextInt( ( int ) bitsImg.dimension( 1 ) );
			raBits.setPosition( new int[] { x, y } );
			raBits.get().set( false );
		}
		ImageJFunctions.show( bitsImg, "BitsSource" );

		// Dilate to new image
		final Img< BitType > imgBits3 = Erosion.erode( bitsImg, strel, 1 );
		ImageJFunctions.show( imgBits3, "BitsErodedToNewImg" );

	}

	public static final void benchmark( final String[] args )
	{
		final int ntrials = 10;

		final Shape strel = new DiamondShape( 3 );

		final ArrayImg< FloatType, FloatArray > img = ArrayImgs.floats( new long[] { 200, 200 } );
		for ( final FloatType pixel : img )
		{
			pixel.set( 1f );
		}
		final ArrayRandomAccess< FloatType > ra = img.randomAccess();

		final ArrayImg< BitType, LongArray > bitsImg = ArrayImgs.bits( new long[] { img.dimension( 0 ), img.dimension( 1 ) } );
		for ( final BitType pixelB : bitsImg )
		{
			pixelB.set( true );
		}
		final ArrayRandomAccess< BitType > raBits = bitsImg.randomAccess(); // LOL

		final Random ran = new Random( 1l );
		for ( int i = 0; i < 1000; i++ )
		{
			final int x = ran.nextInt( ( int ) img.dimension( 0 ) );
			final int y = ran.nextInt( ( int ) img.dimension( 1 ) );

			ra.setPosition( new int[] { x, y } );
			ra.get().set( 0f );

			raBits.setPosition( ra );
			raBits.get().set( false );
		}

		// Dilate to new image
		Img< BitType > imgBits3 = Erosion.erode( bitsImg, strel, 1 );
		long start = System.currentTimeMillis();
		for ( int i = 0; i < ntrials; i++ )
		{
			imgBits3 = Erosion.erode( bitsImg, strel, 1 );
		}
		long end = System.currentTimeMillis();
		System.out.println( "BitType time: " + ( ( end - start ) / ntrials ) + " ms." );

		Img< FloatType > img3 = Erosion.erode( img, strel, 1 );
		start = System.currentTimeMillis();
		for ( int i = 0; i < ntrials; i++ )
		{
			img3 = Erosion.erode( img, strel, 1 );
		}
		end = System.currentTimeMillis();
		System.out.println( "FloatType time: " + ( ( end - start ) / ntrials ) + " ms." );

		ImageJ.main( args );
		ImageJFunctions.show( img3, "Float" );
		ImageJFunctions.show( imgBits3, "Bit" );
	}
}
