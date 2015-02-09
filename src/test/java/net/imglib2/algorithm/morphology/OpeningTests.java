package net.imglib2.algorithm.morphology;

import ij.ImageJ;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.SCIFIOImgPlus;

import java.util.List;
import java.util.Random;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.algorithm.binary.Thresholder;
import net.imglib2.algorithm.neighborhood.DiamondShape;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
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
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class OpeningTests
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
		final List< SCIFIOImgPlus< FloatType >> imgs = new
				ImgOpener().openImgs( fn, new ArrayImgFactory< FloatType >(), new
						FloatType() );
		final Img< FloatType > img = imgs.get( 0 ).getImg();

//		final ArrayImg< FloatType, FloatArray > img = ArrayImgs.floats( new long[] { 800, 600 } );
//		for ( final FloatType pixel : img )
//		{
//			pixel.set( 255f );
//		}
//		final ArrayRandomAccess< FloatType > ra = img.randomAccess();
//		final Random ran = new Random( 1l );
//		for ( int i = 0; i < 100; i++ )
//		{
//			final int x = ran.nextInt( ( int ) img.dimension( 0 ) );
//			final int y = ran.nextInt( ( int ) img.dimension( 1 ) );
//			ra.setPosition( new int[] { x, y } );
//			ra.get().set( 0f );
//		}

		ImageJFunctions.show( img, "Source" );

		final ExtendedRandomAccessibleInterval< FloatType, Img< FloatType >> extendZero = Views.extendZero( img );

		// Open to provided target
		final Interval interval2 = FinalInterval.createMinSize( new long[] { 280, 200, 185, 100 } );
		final Img< FloatType > img2 = img.factory().create( interval2, new FloatType() );
		final long[] translation = new long[ interval2.numDimensions() ];
		interval2.min( translation );
		final IntervalView< FloatType > translate = Views.translate( img2, translation );
		Opening.open( extendZero, translate, strel, 1 );
		ImageJFunctions.show( img2, "OpenedToTarget" );

		// Open to new image
		final Img< FloatType > img3 = Opening.open( img, strel, 1 );
		ImageJFunctions.show( img3, "OpenedToNewImg" );

		// Open in place
		final Interval interval = FinalInterval.createMinSize( new long[] { 100, -10, 200, 200 } );
		Opening.openInPlace( img, interval, strel, 1 );
		ImageJFunctions.show( img, "OpenedInPlace" );

		// BitType
		final Img< BitType > bitImg = Thresholder.threshold( img, new FloatType( 200f ), true, 1 );
		ImageJFunctions.show( bitImg, "BitSource" );

		final ExtendedRandomAccessibleInterval< BitType, Img< BitType >> bitExtendZero = Views.extendZero( bitImg );

		// Open to provided target
		final Img< BitType > bitImg2 = bitImg.factory().create( interval2, new BitType() );
		interval2.min( translation );
		final IntervalView< BitType > bitTranslate = Views.translate( bitImg2, translation );
		Opening.open( bitExtendZero, bitTranslate, strel, 1 );
		ImageJFunctions.show( bitImg2, "BitOpenedToTarget" );

		// Open to new image
		final Img< BitType > bitImg3 = Opening.open( bitImg, strel, 1 );
		ImageJFunctions.show( bitImg3, "bitOpenedToNewImg" );

		// Open in place
		Opening.openInPlace( bitImg, interval, strel, 1 );
		ImageJFunctions.show( bitImg, "OpenedInPlace" );
	}


	public static void show( final String[] args ) throws ImgIOException
	{
		ImageJ.main( args );
//		final Shape strel = new DiamondShape( 3 );
		final Shape strel = new HyperSphereShape( 6 );

		final String fn = "DrosophilaWing.tif";
		final List< SCIFIOImgPlus< FloatType >> imgs = new
				ImgOpener().openImgs( fn, new ArrayImgFactory< FloatType >(), new
						FloatType() );
		final Img< FloatType > img = imgs.get( 0 ).getImg();

//		final ArrayImg< FloatType, FloatArray > img = ArrayImgs.floats( new long[] { 800, 600 } );
//		for ( final FloatType pixel : img )
//		{
//			pixel.set( 255f );
//		}
//		final ArrayRandomAccess< FloatType > ra = img.randomAccess();
//		final Random ran = new Random( 1l );
//		for ( int i = 0; i < 100; i++ )
//		{
//			final int x = ran.nextInt( ( int ) img.dimension( 0 ) );
//			final int y = ran.nextInt( ( int ) img.dimension( 1 ) );
//			ra.setPosition( new int[] { x, y } );
//			ra.get().set( 0f );
//		}

		ImageJFunctions.show( img, "Source" );

		final ExtendedRandomAccessibleInterval< FloatType, Img< FloatType >> extendZero = Views.extendZero( img );

		// Open to provided target
		final Interval interval2 = FinalInterval.createMinSize( new long[] { 280, 200, 185, 100 } );
		final Img< FloatType > img2 = img.factory().create( interval2, new FloatType() );
		final long[] translation = new long[ interval2.numDimensions() ];
		interval2.min( translation );
		final IntervalView< FloatType > translate = Views.translate( img2, translation );
		Opening.open( extendZero, translate, strel, 1 );
		ImageJFunctions.show( img2, "OpenedToTarget" );

		// Open to new image
		final Img< FloatType > img3 = Opening.open( img, strel, 1 );
		ImageJFunctions.show( img3, "OpenedToNewImg" );

		// Open in place
		final Interval interval = FinalInterval.createMinSize( new long[] { 100, -10, 200, 200 } );
		Opening.openInPlace( img, interval, strel, 1 );
		ImageJFunctions.show( img, "OpenedInPlace" );

		// BitType
		final Img< BitType > bitImg = Thresholder.threshold( img, new FloatType( 200f ), true, 1 );
		ImageJFunctions.show( bitImg, "BitSource" );

		final ExtendedRandomAccessibleInterval< BitType, Img< BitType >> bitExtendZero = Views.extendZero( bitImg );

		// Open to provided target
		final Img< BitType > bitImg2 = bitImg.factory().create( interval2, new BitType() );
		interval2.min( translation );
		final IntervalView< BitType > bitTranslate = Views.translate( bitImg2, translation );
		Opening.open( bitExtendZero, bitTranslate, strel, 1 );
		ImageJFunctions.show( bitImg2, "BitOpenedToTarget" );

		// Open to new image
		final Img< BitType > bitImg3 = Opening.open( bitImg, strel, 1 );
		ImageJFunctions.show( bitImg3, "bitOpenedToNewImg" );

		// Open in place
		Opening.openInPlace( bitImg, interval, strel, 1 );
		ImageJFunctions.show( bitImg, "OpenedInPlace" );
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
		Img< BitType > imgBits3 = Opening.open( bitsImg, strel, 1 );
		long start = System.currentTimeMillis();
		for ( int i = 0; i < ntrials; i++ )
		{
			imgBits3 = Opening.open( bitsImg, strel, 1 );
		}
		long end = System.currentTimeMillis();
		System.out.println( "BitType time: " + ( ( end - start ) / ntrials ) + " ms." );

		Img< FloatType > img3 = Opening.open( img, strel, 1 );
		start = System.currentTimeMillis();
		for ( int i = 0; i < ntrials; i++ )
		{
			img3 = Opening.open( img, strel, 1 );
		}
		end = System.currentTimeMillis();
		System.out.println( "FloatType time: " + ( ( end - start ) / ntrials ) + " ms." );

		ImageJ.main( args );
		ImageJFunctions.show( img3, "Float" );
		ImageJFunctions.show( imgBits3, "Bit" );
	}
}
