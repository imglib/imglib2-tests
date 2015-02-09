package net.imglib2.algorithm.morphology;

import ij.ImageJ;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.SCIFIOImgPlus;

import java.io.File;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class BlackTopHatTests
{
	public static void main( final String[] args ) throws ImgIOException
	{
		ImageJ.main( args );
		final File file = new File( "DrosophilaWing.tif" );
		final SCIFIOImgPlus img = new ImgOpener().openImgs( file.getAbsolutePath() ).get( 0 );
		final Img< UnsignedByteType > imgInv = img.copy();
		final Cursor< UnsignedByteType > cursor = img.cursor();
		final Cursor< UnsignedByteType > cursor2 = imgInv.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor2.fwd();
			cursor2.get().set( 255 - cursor.get().get() );
		}

		final Shape strel = new HyperSphereShape( 5 );

		/*
		 * To new Img
		 */

		ImageJFunctions.show( imgInv, "Source" );

		final Img blackTopHat = BlackTopHat.blackTopHat( imgInv, strel, 1 );
		ImageJFunctions.show( blackTopHat, "BlackTopHatToNewImg" );

		/*
		 * In place
		 */

		final Interval interval = FinalInterval.createMinSize( new long[] { 7, 35, 88, 32 } );

		final Img copy2 = imgInv.copy();
		BlackTopHat.blackTopHatInPlace( copy2, interval, strel, 1 );
		ImageJFunctions.show( copy2, "BlackTopHatInPlace" );

		/*
		 * To target
		 */

		final Img img2 = img.factory().create( interval, new UnsignedByteType() );
		final long[] translation = new long[ interval.numDimensions() ];
		interval.min( translation );
		final IntervalView translate = Views.translate( img2, translation );
		BlackTopHat.blackTopHat( imgInv, translate, strel, 1 );
		ImageJFunctions.show( img2, "BlackTopHatToTarget" );

	}

}
