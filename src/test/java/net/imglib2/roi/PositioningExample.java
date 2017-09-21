package net.imglib2.roi;

import java.io.IOException;

import net.imglib2.RandomAccess;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;

public class PositioningExample
{
	PositioningExample()
	{
		new ij.ImageJ();

		final Img< BitType > mask = ArrayImgs.bits( new long[] { 9, 9 } );
		final RandomAccess< Neighborhood< BitType > > a = new HyperSphereShape( 4 ).neighborhoodsRandomAccessible( mask ).randomAccess();
		a.setPosition( new long[] { 4, 4 } );
		a.get().forEach( t -> t.set( true ) );
		ImageJFunctions.show( mask );

		{
			final Img< IntType > output = ArrayImgs.ints( new long[] { 50, 50 } );
			final PositionableIterableRegion< BitType > roi = Regions.positionable( mask );
			Regions.sample( roi, output ).forEach( t -> t.set( 255 ) );
			roi.setPosition( 20, 0 );
			Regions.sample( roi, output ).forEach( t -> t.set( 255 ) );
			roi.setPosition( 20, 1 );
			Regions.sample( roi, output ).forEach( t -> t.set( 255 ) );
			roi.setPosition( 0, 0 );
			Regions.sample( roi, output ).forEach( t -> t.set( 255 ) );
			ImageJFunctions.show( output );
		}

		{
			final Img< IntType > output = ArrayImgs.ints( new long[] { 50, 50 } );
			final PositionableIterableRegion< BitType > roi = Regions.positionable( mask );
			roi.origin().setPosition( new long[] { 4, 4 } );
			roi.setPosition( 4, 0 );
			roi.setPosition( 4, 1 );
			Regions.sample( roi, output ).forEach( t -> t.set( 255 ) );
			roi.setPosition( 24, 0 );
			Regions.sample( roi, output ).forEach( t -> t.set( 255 ) );
			roi.setPosition( 24, 1 );
			Regions.sample( roi, output ).forEach( t -> t.set( 255 ) );
			roi.setPosition( 4, 0 );
			Regions.sample( roi, output ).forEach( t -> t.set( 255 ) );
			ImageJFunctions.show( output );
		}

		{
			final Img< IntType > output = ArrayImgs.ints( new long[] { 50, 50 } );
			final PositionableIterableRegion< BitType > roi = Regions.positionable( mask );
			roi.origin().setPosition( new long[] { 4, 4 } );
			final PositionableIterableInterval< IntType > sampled = Regions.sample( roi, output );
			sampled.setPosition( 4, 0 );
			sampled.setPosition( 4, 1 );
			sampled.forEach( t -> t.set( 255 ) );
			sampled.setPosition( 24, 0 );
			sampled.forEach( t -> t.set( 255 ) );
			sampled.setPosition( 24, 1 );
			sampled.forEach( t -> t.set( 255 ) );
			sampled.setPosition( 4, 0 );
			sampled.forEach( t -> t.set( 255 ) );
			ImageJFunctions.show( output );
		}
	}

	// Just to run it
	public static void main( final String... args ) throws IOException
	{
		new PositioningExample();
	}
}
