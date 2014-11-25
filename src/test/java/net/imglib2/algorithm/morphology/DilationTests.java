package net.imglib2.algorithm.morphology;

import ij.ImageJ;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.SCIFIOImgPlus;

import java.util.List;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.algorithm.morphology.neighborhoods.DiamondShape;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class DilationTests
{
	public static void main( final String[] args ) throws ImgIOException
	{
		final String fn = "DrosophilaWing.tif";
		final List< SCIFIOImgPlus< FloatType >> imgs = new ImgOpener().openImgs( fn, new ArrayImgFactory< FloatType >(), new FloatType() );
		final Img< FloatType > img = imgs.get( 0 ).getImg();
		final Shape strel = new DiamondShape( 5 );
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
		final Interval interval = FinalInterval.createMinSize( new long[] { 600, 4, 185, 100 } );
		Dilation.dilateInPlace( img, interval, strel, minVal, 1 );
		ImageJFunctions.show( img, "DilatedInPlace" );

		ImageJFunctions.show( img, "SourceAgain" );
	}
}
