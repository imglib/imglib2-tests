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
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class DilationTests
{
	public static void main( final String[] args ) throws ImgIOException
	{
		// final String fn = "DrosophilaWing.tif";
		// final List< SCIFIOImgPlus< FloatType >> imgs = new
		// ImgOpener().openImgs( fn, new ArrayImgFactory< FloatType >(), new
		// FloatType() );
		// final Img< FloatType > img = imgs.get( 0 ).getImg();

		final ArrayImg< FloatType, FloatArray > img = ArrayImgs.floats( new long[] { 800, 600 } );
		final ArrayRandomAccess< FloatType > ra = img.randomAccess();
		final Random ran = new Random( 1l );
		for ( int i = 0; i < 100; i++ )
		{
			final int x = ran.nextInt( ( int ) img.dimension( 0 ) );
			final int y = ran.nextInt( ( int ) img.dimension( 1 ) );
			ra.setPosition( new int[] { x, y } );
			ra.get().set( 255f );
		}

		final Shape strel = new DiamondShape( 9 );
		final FloatType minVal = new FloatType( 0 );

		ImageJ.main( args );
		ImageJFunctions.show( img, "Source" );

		// Dilate to provided target
		final Interval interval2 = FinalInterval.createMinSize( new long[] { 280, 200, 185, 100 } );
		final Img< FloatType > img2 = img.factory().create( interval2, new FloatType() );
		final long[] translation = new long[ interval2.numDimensions() ];
		interval2.min( translation );
		final IntervalView< FloatType > translate = Views.translate( img2, translation );
		Dilation.dilate( img, translate, strel, minVal, 1 );
		ImageJFunctions.show( img2, "DilatedToTarget" );

		// Dilate to new image
		final Img< FloatType > img3 = Dilation.dilate( img, strel, minVal, 1 );
		ImageJFunctions.show( img3, "DilatedToNewImg" );

		// Dilate to new image FULL version.
		final Img< FloatType > img4 = Dilation.dilateFull( img, strel, minVal, 1 );
		ImageJFunctions.show( img4, "DilatedToNewImgFULL" );

		// Dilate in place
		final Interval interval = FinalInterval.createMinSize( new long[] { 100, -10, 200, 200 } );
		Dilation.dilateInPlace( img, interval, strel, minVal, 1 );
		ImageJFunctions.show( img, "DilatedInPlace" );

		ImageJFunctions.show( img, "SourceAgain" );
	}
}
