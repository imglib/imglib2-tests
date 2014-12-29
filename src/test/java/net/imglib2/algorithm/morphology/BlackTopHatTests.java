package net.imglib2.algorithm.morphology;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.algorithm.region.localneighborhood.HyperSphereShape;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class BlackTopHatTests
{
	public static void main( final String[] args )
	{
		ImageJ.main( args );
		final File file = new File( "/Users/tinevez/Desktop/iconas/Data/Uneven.tif" );
		final ImagePlus imp = IJ.openImage( file.getAbsolutePath() );
		final Object obj = ImagePlusAdapter.wrap( imp );

		@SuppressWarnings( "unchecked" )
		final Img< UnsignedByteType > img = ( Img< UnsignedByteType > ) obj;
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
