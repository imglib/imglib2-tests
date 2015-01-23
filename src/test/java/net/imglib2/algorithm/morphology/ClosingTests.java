package net.imglib2.algorithm.morphology;

import ij.ImageJ;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.SCIFIOImgPlus;

import java.util.List;
import java.util.Random;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.algorithm.morphology.neighborhoods.DiamondShape;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class ClosingTests
{
	public static void main( final String[] args ) throws ImgIOException
	{
		show( args );
		benchmark( args );
		chain( args );
	}

	public static void chain( final String[] args ) throws ImgIOException
	{
		final List< Shape > strel = StructuringElements.disk( 6, 2, 4 );
		for ( final Shape shape : strel )
		{
			System.out.println( shape );
			System.out.println( MorphologyUtils.printNeighborhood( shape, 2 ) );
		}

		ImageJ.main( args );

		final String fn = "DrosophilaWing.tif";
		final List< SCIFIOImgPlus< UnsignedByteType >> imgs = new
				ImgOpener().openImgs( fn, new ArrayImgFactory< UnsignedByteType >(), new
						UnsignedByteType() );
		final Img< UnsignedByteType > img = imgs.get( 0 ).getImg();

//		final ArrayImg< UnsignedByteType, FloatArray > img = ArrayImgs.floats( new long[] { 800, 600 } );
//		for ( final UnsignedByteType pixel : img )
//		{
//			pixel.set( 255f );
//		}
//		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
//		final Random ran = new Random( 1l );
//		for ( int i = 0; i < 100; i++ )
//		{
//			final int x = ran.nextInt( ( int ) img.dimension( 0 ) );
//			final int y = ran.nextInt( ( int ) img.dimension( 1 ) );
//			ra.setPosition( new int[] { x, y } );
//			ra.get().set( 0f );
//		}

		ImageJFunctions.show( img, "Source" );

		final ExtendedRandomAccessibleInterval< UnsignedByteType, Img< UnsignedByteType >> extendZero = Views.extendZero( img );

		// Close to provided target
		final Interval interval2 = FinalInterval.createMinSize( new long[] { 280, 200, 185, 100 } );
		final Img< UnsignedByteType > img2 = img.factory().create( interval2, new UnsignedByteType() );
		final long[] translation = new long[ interval2.numDimensions() ];
		interval2.min( translation );
		final IntervalView< UnsignedByteType > translate = Views.translate( img2, translation );
		Closing.close( extendZero, translate, strel, 1 );
		ImageJFunctions.show( img2, "ClosedToTarget" );

		// Close to new image
		final Img< UnsignedByteType > img3 = Closing.close( img, strel, 1 );
		ImageJFunctions.show( img3, "ClosedToNewImg" );

		// Close in place
		final Interval interval = FinalInterval.createMinSize( new long[] { 100, -10, 200, 200 } );
		Closing.closeInPlace( img, interval, strel, 1 );
		ImageJFunctions.show( img, "ClosedInPlace" );

		// BitType
		final Img< BitType > bitImg = Thresholder.threshold( img, new UnsignedByteType( 100 ), true, 1 );
		ImageJFunctions.show( bitImg, "BitSource" );

		final ExtendedRandomAccessibleInterval< BitType, Img< BitType >> bitExtendZero = Views.extendZero( bitImg );

		// Close to provided target
		final Img< BitType > bitImg2 = bitImg.factory().create( interval2, new BitType() );
		interval2.min( translation );
		final IntervalView< BitType > bitTranslate = Views.translate( bitImg2, translation );
		Closing.close( bitExtendZero, bitTranslate, strel, 1 );
		ImageJFunctions.show( bitImg2, "BitClosedToTarget" );

		// Close to new image
		final Img< BitType > bitImg3 = Closing.close( bitImg, strel, 1 );
		ImageJFunctions.show( bitImg3, "bitClosedToNewImg" );

		// Close in place
		Closing.closeInPlace( bitImg, interval, strel, 1 );
		ImageJFunctions.show( bitImg, "ClosedInPlace" );
	}


	public static void show( final String[] args ) throws ImgIOException
	{
		ImageJ.main( args );
		final Shape strel = new DiamondShape( 3 );
//		final Shape strel = new HyperSphereShape( 6 );

		final String fn = "DrosophilaWing.tif";
		final List< SCIFIOImgPlus< UnsignedByteType >> imgs = new
				ImgOpener().openImgs( fn, new ArrayImgFactory< UnsignedByteType >(), new
						UnsignedByteType() );
		final Img< UnsignedByteType > img = imgs.get( 0 ).getImg();

//		final ArrayImg< UnsignedByteType, FloatArray > img = ArrayImgs.floats( new long[] { 800, 600 } );
//		for ( final UnsignedByteType pixel : img )
//		{
//			pixel.set( 255f );
//		}
//		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
//		final Random ran = new Random( 1l );
//		for ( int i = 0; i < 100; i++ )
//		{
//			final int x = ran.nextInt( ( int ) img.dimension( 0 ) );
//			final int y = ran.nextInt( ( int ) img.dimension( 1 ) );
//			ra.setPosition( new int[] { x, y } );
//			ra.get().set( 0f );
//		}

		ImageJFunctions.show( img, "Source" );

		final ExtendedRandomAccessibleInterval< UnsignedByteType, Img< UnsignedByteType >> extendZero = Views.extendZero( img );

		// Close to provided target
		final Interval interval2 = FinalInterval.createMinSize( new long[] { 280, 200, 185, 100 } );
		final Img< UnsignedByteType > img2 = img.factory().create( interval2, new UnsignedByteType() );
		final long[] translation = new long[ interval2.numDimensions() ];
		interval2.min( translation );
		final IntervalView< UnsignedByteType > translate = Views.translate( img2, translation );
		Closing.close( extendZero, translate, strel, 1 );
		ImageJFunctions.show( img2, "ClosedToTarget" );

		// Close to new image
		final Img< UnsignedByteType > img3 = Closing.close( img, strel, 1 );
		ImageJFunctions.show( img3, "ClosedToNewImg" );

		// Close in place
		final Interval interval = FinalInterval.createMinSize( new long[] { 100, -10, 200, 200 } );
		Closing.closeInPlace( img, interval, strel, 1 );
		ImageJFunctions.show( img, "ClosedInPlace" );

		// BitType
		final Img< BitType > bitImg = Thresholder.threshold( img, new UnsignedByteType( 100 ), true, 1 );
		ImageJFunctions.show( bitImg, "BitSource" );

		final ExtendedRandomAccessibleInterval< BitType, Img< BitType >> bitExtendZero = Views.extendZero( bitImg );

		// Close to provided target
		final Img< BitType > bitImg2 = bitImg.factory().create( interval2, new BitType() );
		interval2.min( translation );
		final IntervalView< BitType > bitTranslate = Views.translate( bitImg2, translation );
		Closing.close( bitExtendZero, bitTranslate, strel, 1 );
		ImageJFunctions.show( bitImg2, "BitClosedToTarget" );

		// Close to new image
		final Img< BitType > bitImg3 = Closing.close( bitImg, strel, 1 );
		ImageJFunctions.show( bitImg3, "bitClosedToNewImg" );

		// Close in place
		Closing.closeInPlace( bitImg, interval, strel, 1 );
		ImageJFunctions.show( bitImg, "ClosedInPlace" );
	}

	public static final void benchmark( final String[] args )
	{
		final int ntrials = 10;

		final Shape strel = new DiamondShape( 3 );

		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( new long[] { 200, 200 } );
		for ( final UnsignedByteType pixel : img )
		{
			pixel.set( 1 );
		}
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();

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
			ra.get().set( 0 );

			raBits.setPosition( ra );
			raBits.get().set( false );
		}

		// Dilate to new image
		Img< BitType > imgBits3 = Closing.close( bitsImg, strel, 1 );
		long start = System.currentTimeMillis();
		for ( int i = 0; i < ntrials; i++ )
		{
			imgBits3 = Closing.close( bitsImg, strel, 1 );
		}
		long end = System.currentTimeMillis();
		System.out.println( "BitType time: " + ( ( end - start ) / ntrials ) + " ms." );

		Img< net.imglib2.type.numeric.integer.UnsignedByteType > img3 = Closing.close( img, strel, 1 );
		start = System.currentTimeMillis();
		for ( int i = 0; i < ntrials; i++ )
		{
			img3 = Closing.close( img, strel, 1 );
		}
		end = System.currentTimeMillis();
		System.out.println( "UnsignedByteType time: " + ( ( end - start ) / ntrials ) + " ms." );

		ImageJ.main( args );
		ImageJFunctions.show( img3, "Float" );
		ImageJFunctions.show( imgBits3, "Bit" );
	}
}
