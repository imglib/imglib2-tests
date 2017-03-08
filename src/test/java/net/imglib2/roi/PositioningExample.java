package net.imglib2.roi;

import java.io.IOException;

import net.imglib2.RandomAccess;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;

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
			final Img< BitType > output = ArrayImgs.bits( new long[] { 50, 50 } );
			final PositionableIterableRegion< BitType > roi = Regions.positionable( mask );
			Regions.sample( roi, output ).forEach( t -> t.set( true ) );
			roi.setPosition( 20, 0 );
			Regions.sample( roi, output ).forEach( t -> t.set( true ) );
			roi.setPosition( 20, 1 );
			Regions.sample( roi, output ).forEach( t -> t.set( true ) );
			roi.setPosition( 0, 0 );
			Regions.sample( roi, output ).forEach( t -> t.set( true ) );
			ImageJFunctions.show( output );
		}

		{
			final Img< BitType > output = ArrayImgs.bits( new long[] { 50, 50 } );
			final PositionableIterableInterval< BitType > roi = Regions.sample( Regions.positionable( mask ), output );
			roi.origin().setPosition( new long[] { 4, 4 } );
			roi.setPosition( 4, 0 );
			roi.setPosition( 4, 1 );
			roi.forEach( t -> t.set( true ) );
			roi.setPosition( 24, 0 );
			roi.forEach( t -> t.set( true ) );
			roi.setPosition( 24, 1 );
			roi.forEach( t -> t.set( true ) );
			roi.setPosition( 4, 0 );
			roi.forEach( t -> t.set( true ) );
			ImageJFunctions.show( output );
		}
	}

	// Just to run it
	public static void main( final String... args ) throws IOException
	{
		new PositioningExample();
	}
}
