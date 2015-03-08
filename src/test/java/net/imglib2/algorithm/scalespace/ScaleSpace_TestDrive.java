package net.imglib2.algorithm.scalespace;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;

import java.util.Collection;
import java.util.Random;

import net.imglib2.RandomAccess;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.HyperSphereShape.NeighborhoodsAccessible;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.converter.Converter;
import net.imglib2.converter.RealFloatConverter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class ScaleSpace_TestDrive
{

	public static void main( final String[] args )
	{
		ImageJ.main( args );

		/*
		 * Prepare image
		 */

		final int width = 512;
		final int height = 512;
		final int R = 20;
		final int N_SPOTS = 30;
		final ImgFactory< UnsignedByteType > factory = new ArrayImgFactory< UnsignedByteType >();
		final Img< UnsignedByteType > img = factory.create( new int[] { width, height }, new UnsignedByteType() );

		final Random ran = new Random();
		for ( int i = 0; i < N_SPOTS; i++ )
		{
			final long radius = Math.round( R + R / 4 * ran.nextGaussian() );
			final HyperSphereShape shape = new HyperSphereShape( radius );
			final NeighborhoodsAccessible< UnsignedByteType > neighborhoodsAccessible =
					shape.neighborhoodsRandomAccessible( Views.extendZero( img ) );
			final RandomAccess< Neighborhood< UnsignedByteType >> randomAccess = neighborhoodsAccessible.randomAccess();
			randomAccess.setPosition( ran.nextInt( width ), 0 );
			randomAccess.setPosition( ran.nextInt( height ), 1 );
			final int val = ran.nextInt( 256 );
			for ( final UnsignedByteType pixel : randomAccess.get() )
			{
				pixel.set( val );
			}
		}

		final ImagePlus imp = ImageJFunctions.wrap( img, "Source" );
		imp.show();

		/*
		 * Execute scale space computation.
		 */

		final double initialSigma = 2;
		final Converter< UnsignedByteType, FloatType > converter = new RealFloatConverter< UnsignedByteType >();
		final ScaleSpace< UnsignedByteType > scaleSpace = new ScaleSpace< UnsignedByteType >( img, converter, initialSigma, 0.03d );
		scaleSpace.setNumThreads( 1 );

		System.out.println( "Starting scale space computation." );
		if ( !scaleSpace.checkInput() || !scaleSpace.process() )
		{
			System.err.println( "ScaleSpace failed: " + scaleSpace.getErrorMessage() );
			return;
		}
		System.out.println( "Done in " + scaleSpace.getProcessingTime() + " ms." );

		/*
		 * Show images
		 */

		final Img< FloatType > results = scaleSpace.getResult();
		ImageJFunctions.wrapFloat( results, "Scale space" ).show();

		final Overlay overlay = new Overlay();
		imp.setOverlay( overlay );

		/*
		 * Overlay results on source image
		 */

		final Collection< RefinedPeak< DifferenceOfGaussianPeak >> peaks = scaleSpace.getPeaks();
		for ( final RefinedPeak< DifferenceOfGaussianPeak > peak : peaks )
		{

			if ( !peak.getOriginalPeak().isMax() )
			{
				continue; // only maxima
			}

			final double diameter = 2 * peak.getDoublePosition( 2 ) * Math.sqrt( img.numDimensions() );
			final Roi roi = new OvalRoi(
					0.5 + peak.getDoublePosition( 0 ) - diameter / 2,
					0.5 + peak.getDoublePosition( 1 ) - diameter / 2,
					diameter, diameter );
			overlay.add( roi );
		}

		imp.show();
	}

}
