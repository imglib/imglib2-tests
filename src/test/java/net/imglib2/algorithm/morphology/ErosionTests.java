package net.imglib2.algorithm.morphology;

import ij.ImageJ;
import io.scif.img.ImgIOException;

import java.util.Random;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.algorithm.morphology.neighborhoods.DiamondShape;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class ErosionTests
{
	public static void main( final String[] args ) throws ImgIOException
	{
//		show( args );
//		benchmark( args );
		chain( args );
	}

	private static void chain( final String[] args )
	{
		ImageJ.main( args );

		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 50l, 50l );
		for ( final UnsignedByteType pixel : img )
		{
			pixel.set( 255 );
		}
		final ArrayRandomAccess< UnsignedByteType > randomAccess = img.randomAccess();
		randomAccess.setPosition( new int[] { 0, 25 } );
		randomAccess.get().set( 0 );
		randomAccess.setPosition( new int[] { 35, 25 } );
		randomAccess.get().set( 0 );

		final DiamondShape diamondShape = new DiamondShape( 8 );
		ImageJFunctions.show( img, "Source" );

		// New Source
		ImageJFunctions.show( Erosion.erode( img, StructuringElements.diamond( 8, 2, true ), 1 ), "NewSourceDecomp" );
		ImageJFunctions.show( Erosion.erode( img, StructuringElements.diamond( 8, 2, false ), 1 ), "NewSourceStraight" );
		ImageJFunctions.show( Erosion.erode( img, diamondShape, 1 ), "NewSourceSingle" );

		// Full
		ImageJFunctions.show( Erosion.erodeFull( img, StructuringElements.diamond( 8, 2, true ), 1 ), "NewFullSourceDecomp" );
		ImageJFunctions.show( Erosion.erodeFull( img, StructuringElements.diamond( 8, 2, false ), 1 ), "NewFullSourceStraight" );
		ImageJFunctions.show( Erosion.erodeFull( img, diamondShape, 1 ), "NewFullSourceSingle" );

		// To target
		final Interval interval = FinalInterval.createMinSize( 10, 10, 20, 20 );
		final long[] min = new long[ interval.numDimensions() ];
		interval.min( min );

		final Img< UnsignedByteType > result1 = img.factory().create( interval, img.firstElement().copy() );
		final IntervalView< UnsignedByteType > target1 = Views.translate( result1, min );
		Erosion.erode( img, target1, StructuringElements.diamond( 8, 2, true ), 1 );
		ImageJFunctions.show( result1, "ToTargetDecomp" );

		final Img< UnsignedByteType > result2 = img.factory().create( interval, img.firstElement().copy() );
		final IntervalView< UnsignedByteType > target2 = Views.translate( result2, min );
		Erosion.erode( img, target2, StructuringElements.diamond( 8, 2, false ), 1 );
		ImageJFunctions.show( result2, "ToTargetStraight" );

		final Img< UnsignedByteType > result3 = img.factory().create( interval, img.firstElement().copy() );
		final IntervalView< UnsignedByteType > target3 = Views.translate( result3, min );
		Erosion.erode( img, target3, diamondShape, 1 );
		ImageJFunctions.show( result3, "ToTargetSingle" );

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
