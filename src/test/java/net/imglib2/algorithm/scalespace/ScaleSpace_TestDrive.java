package net.imglib2.algorithm.scalespace;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;

import java.awt.Color;
import java.util.Collection;
import java.util.Random;

import net.imglib2.RandomAccess;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.HyperSphereShape.NeighborhoodsAccessible;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.converter.Converter;
import net.imglib2.converter.RealFloatConverter;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class ScaleSpace_TestDrive
{

	public static void main( final String[] args ) throws IncompatibleTypeException
	{
		ImageJ.main( args );
		uniqueBlob();
		rowsOfBlobs();
		multipleBlobs();
	}

	public static void rowsOfBlobs() throws IncompatibleTypeException
	{
		final int width = 512;
		final int height = 512;
		final ImgFactory< UnsignedByteType > factory = new ArrayImgFactory< UnsignedByteType >();
		final Img< UnsignedByteType > img = factory.create( new int[] { width, height }, new UnsignedByteType() );

		int radius = 20;
		for ( int r = 0; r < 4; r++ )
		{
			for ( int c = 0; c < 4; c++ )
			{
				radius += 2;
				final HyperSphereShape shape = new HyperSphereShape( radius );
				final NeighborhoodsAccessible< UnsignedByteType > neighborhoodsAccessible =
						shape.neighborhoodsRandomAccessible( Views.extendZero( img ) );
				final RandomAccess< Neighborhood< UnsignedByteType >> randomAccess = neighborhoodsAccessible.randomAccess();
				randomAccess.setPosition( c * width / 4 + width / 8, 0 );
				randomAccess.setPosition( r * height / 4 + height / 8, 1 );
				final int val = 160;
				for ( final UnsignedByteType pixel : randomAccess.get() )
				{
					pixel.set( val );
				}

			}
		}

		Gauss3.gauss( 1d, Views.extendMirrorSingle( img ), img );
		final Random ran = new Random();
		for ( final UnsignedByteType pixel : img )
		{
			pixel.set( ( int ) ( ( pixel.get() + 20 ) * ( 1 + ran.nextDouble() / 4 ) ) );
		}

		analyze( img );
	}

	public static void uniqueBlob() throws IncompatibleTypeException
	{

		final int width = 512;
		final int height = 512;
		final int radius = 20;
		final ImgFactory< UnsignedByteType > factory = new ArrayImgFactory< UnsignedByteType >();
		final Img< UnsignedByteType > img = factory.create( new int[] { width, height }, new UnsignedByteType() );

		final HyperSphereShape shape = new HyperSphereShape( radius );
		final NeighborhoodsAccessible< UnsignedByteType > neighborhoodsAccessible =
				shape.neighborhoodsRandomAccessible( Views.extendZero( img ) );
		final RandomAccess< Neighborhood< UnsignedByteType >> randomAccess = neighborhoodsAccessible.randomAccess();
		randomAccess.setPosition( width / 2, 0 );
		randomAccess.setPosition( height / 2, 1 );
		final int val = 150;
		for ( final UnsignedByteType pixel : randomAccess.get() )
		{
			pixel.set( val );
		}
		Gauss3.gauss( 1d, Views.extendMirrorSingle( img ), img );
		final Random ran = new Random();
		for ( final UnsignedByteType pixel : img )
		{
			pixel.set( ( int ) ( ( pixel.get() + 20 ) * ( 1 + ran.nextDouble() / 2 ) ) );
		}

		analyze( img );
	}

	public static void multipleBlobs() throws IncompatibleTypeException
	{
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
			final int val = ran.nextInt( 150 );
			for ( final UnsignedByteType pixel : randomAccess.get() )
			{
				pixel.set( val );
			}
		}
		Gauss3.gauss( 1d, Views.extendMirrorSingle( img ), img );
		for ( final UnsignedByteType pixel : img )
		{
			pixel.set( ( int ) ( ( pixel.get() + 20 ) * ( 1 + ran.nextDouble() / 2 ) ) );
		}
		analyze( img );
	}

	public static < T extends RealType< T > & NativeType< T > > void analyze( final Img< T > img )
	{
		final ImagePlus imp = ImageJFunctions.wrap( img, "Source" );
		imp.show();

		/*
		 * Execute scale space computation.
		 */

		final double initialSigma = 2.5;
		final Converter< T, FloatType > converter = new RealFloatConverter< T >();
		final ScaleSpace< T > scaleSpace = new ScaleSpace< T >( img, converter, initialSigma, 0.03d, 2d );
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

//		final Img< FloatType > results = scaleSpace.getResult();
//		ImageJFunctions.wrapFloat( results, "Scale space" ).show();


		/*
		 * Overlay results on source image
		 */

		final Overlay overlay = new Overlay();
		imp.setOverlay( overlay );
		overlay.setStrokeColor( Color.GREEN );

		final Collection< Blob > brightBlobs = scaleSpace.getBrightBlobs();
		System.out.println( "Found " + brightBlobs.size() + " bright blobs." );
		for ( final Blob blob : brightBlobs )
		{
			System.out.println( " - " + blob );

			final double diameter = 2 * blob.getRadius();
			final Roi roi = new OvalRoi(
					0.5 + blob.getDoublePosition( 0 ) - diameter / 2,
					0.5 + blob.getDoublePosition( 1 ) - diameter / 2,
					diameter, diameter );

			overlay.add( roi );
			imp.updateAndDraw();
		}
		overlay.setStrokeColor( Color.RED );

		final Collection< Blob > darkBlobs = scaleSpace.getDarkBlobs();
		System.out.println( "Found " + darkBlobs.size() + " dark blobs." );
		for ( final Blob blob : darkBlobs )
		{
			System.out.println( " - " + blob );

			final double diameter = 2 * blob.getRadius();
			final Roi roi = new OvalRoi(
					0.5 + blob.getDoublePosition( 0 ) - diameter / 2,
					0.5 + blob.getDoublePosition( 1 ) - diameter / 2,
					diameter, diameter );

			overlay.add( roi );
			imp.updateAndDraw();
		}
	}

}
