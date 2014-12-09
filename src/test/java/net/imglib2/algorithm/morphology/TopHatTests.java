package net.imglib2.algorithm.morphology;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;
import java.util.List;

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

public class TopHatTests
{
	public static void main( final String[] args )
	{
//		show( args );
		decomp( args );
	}

	private static void decomp( final String[] args )
	{
		ImageJ.main( args );
		final File file = new File( "/Users/tinevez/Desktop/iconas/Data/Uneven.tif" );
		final ImagePlus imp = IJ.openImage( file.getAbsolutePath() );
		final Object obj = ImagePlusAdapter.wrap( imp );
		@SuppressWarnings( "unchecked" )
		final Img< UnsignedByteType > img = ( Img< UnsignedByteType > ) obj;

		final List< Shape > strel = StructuringElements.diamond( 8, 2, true );

		/*
		 * To new Img
		 */

		ImageJFunctions.show( img, "Source" );

		final Img< UnsignedByteType > topHat = TopHat.topHat( img, strel, 1 );
		ImageJFunctions.show( topHat, "WhiteTopHatToNewImg" );

		/*
		 * In place
		 */

		final Interval interval = FinalInterval.createMinSize( new long[] { 30, 50, 88, 32 } );
		final Img< UnsignedByteType > copy = img.copy();
		TopHat.topHatInPlace( copy, interval, strel, 1 );
		ImageJFunctions.show( copy, "WhiteTopHatInPlace" );

		/*
		 * To target
		 */

		final Img< UnsignedByteType > img2 = img.factory().create( interval, new UnsignedByteType() );
		final long[] translation = new long[ interval.numDimensions() ];
		interval.min( translation );
		final IntervalView< UnsignedByteType > translate = Views.translate( img2, translation );
		TopHat.topHat( img, translate, strel, 1 );
		ImageJFunctions.show( img2, "WhiteTopHatToTarget" );
	}

	public static void show( final String[] args )
	{
		ImageJ.main( args );
		final File file = new File( "/Users/tinevez/Desktop/iconas/Data/Uneven.tif" );
		final ImagePlus imp = IJ.openImage( file.getAbsolutePath() );
		final Object obj = ImagePlusAdapter.wrap( imp );
		@SuppressWarnings( "unchecked" )
		final Img< UnsignedByteType > img = ( Img< UnsignedByteType > ) obj;

		final Shape strel = new HyperSphereShape( 5 );

		/*
		 * To new Img
		 */

		ImageJFunctions.show( img, "Source" );

		final Img< UnsignedByteType > topHat = TopHat.topHat( img, strel, 1 );
		ImageJFunctions.show( topHat, "WhiteTopHatToNewImg" );


		/*
		 * In place
		 */

		final Interval interval = FinalInterval.createMinSize( new long[] { 30, 50, 88, 32 } );
		final Img< UnsignedByteType > copy = img.copy();
		TopHat.topHatInPlace( copy, interval, strel, 1 );
		ImageJFunctions.show( copy, "WhiteTopHatInPlace" );

		/*
		 * To target
		 */

		final Img< UnsignedByteType > img2 = img.factory().create( interval, new UnsignedByteType() );
		final long[] translation = new long[ interval.numDimensions() ];
		interval.min( translation );
		final IntervalView< UnsignedByteType > translate = Views.translate( img2, translation );
		TopHat.topHat( img, translate, strel, 1 );
		ImageJFunctions.show( img2, "WhiteTopHatToTarget" );

	}

}
