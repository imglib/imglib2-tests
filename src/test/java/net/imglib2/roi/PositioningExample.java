package net.imglib2.roi;

import java.io.IOException;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.util.TemplateRandomAccessible;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

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




		final Img< BitType > newmask = ArrayImgs.bits( new long[] { 9, 9 } );
		final RandomAccess< IterableInterval< BitType > > a2 = Regions.neighborhoodsRandomAccessible( Views.translate( mask, -4, -4 ), newmask ).randomAccess();
		a2.setPosition( new long[] { 4, 4 } );
		a2.get().forEach( t -> t.set( true ) );
		ImageJFunctions.show( newmask );

		/*
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
		*/

		{
			final Img< IntType > output = ArrayImgs.ints( new long[] { 50, 50 } );
////			final RandomAccessibleInterval< BitType > shiftedmask = Views.translate( mask, -4, -4 );
////			final PositionableIterableRegion< BitType > roi = Regions.itcodepositionable( shiftedmask );
//			final PositionableIterableRegion< BitType > roi = Regions.shiftOrigin( Regions.itcodepositionable( mask ), 4, 4 );
////			roi.origin().setPosition( new long[] { 4, 4 } );
//			final PositionableIterableInterval< IntType > sampled = Regions.sample( roi, output, true );
//			final RandomAccessible< IterableInterval< IntType > > accessible = new TemplateRandomAccessible<>( sampled );
			final RandomAccessible< IterableInterval< IntType > > accessible = Regions.neighborhoodsRandomAccessible( Views.translate( mask, -4, -4 ), output );

			final RandomAccess< IterableInterval< IntType > > ra = accessible.randomAccess();
			ra.setPosition( new long[] { 4, 4 } );
			ra.get().forEach( t -> t.set( 255 ) );
			ra.setPosition( 24, 0 );
			ra.get().forEach( t -> t.set( 255 ) );
			ra.setPosition( 24, 1 );
			ra.get().forEach( t -> t.set( 255 ) );
			ra.setPosition( 4, 0 );
			ra.get().forEach( t -> t.set( 255 ) );
			ImageJFunctions.show( output );
		}

		/*
		{
			final Img< BitType > mask2 = ArrayImgs.bits( new long[] { 50, 50 } );
			final RandomAccess< BitType > a2 = mask2.randomAccess();
			a2.setPosition( new long[] { 4, 4 } );
			a2.get().set( true );
			a2.setPosition( new long[] { 24, 4 } );
			a2.get().set( true );
			a2.setPosition( new long[] { 24, 24 } );
			a2.get().set( true );
			a2.setPosition( new long[] { 4, 24 } );
			a2.get().set( true );
			ImageJFunctions.show( mask2 );

			final Img< IntType > output = ArrayImgs.ints( new long[] { 50, 50 } );
			final PositionableIterableRegion< BitType > roi = Regions.positionable( mask );
			roi.origin().setPosition( new long[] { 4, 4 } );
//			final PositionableIterableInterval< IntType > sampled = Regions.sample( roi, output );
//			final RandomAccessible< IterableInterval< IntType > > accessible = new TemplateRandomAccessible<>( sampled );
//			final IterableInterval< IterableInterval< IntType > > sampled2 = Regions.sample( Regions.iterable( mask2 ), accessible );
//			sampled2.forEach( i -> i.forEach( b -> b.set( 255 ) ) );

			Regions.sample(
					Regions.iterable( mask2 ),
					new TemplateRandomAccessible<>(
							Regions.sample(
									roi, // Regions.offset( Regions.positionable( mask ), -4, -4 )
									output ) ) )
					.forEach( i -> i.forEach( b -> b.set( 255 ) ) );

			ImageJFunctions.show( output );
		}
		*/
	}

	// Just to run it
	public static void main( final String... args ) throws IOException
	{
		new PositioningExample();
	}
}
