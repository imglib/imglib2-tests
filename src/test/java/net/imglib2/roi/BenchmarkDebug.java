package net.imglib2.roi;

import java.io.IOException;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.util.TemplateRandomAccessible;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class BenchmarkDebug
{
	static final long[] dims = new long[] { 100, 100 };

	static final long[] maskdims = new long[] { 11, 11 };

	static final int border = 10;

	public void init()
	{
		final Img< BitType > mask = ArrayImgs.bits( maskdims );
		final Shape shape = new RectangleShape( 5, true );
		final RandomAccess< Neighborhood< BitType > > a = shape.neighborhoodsRandomAccessible( mask ).randomAccess();
		for ( int d = 0; d < mask.numDimensions(); ++d )
			a.setPosition( maskdims[ d ] / 2, d );
		a.get().forEach( t -> t.set( true ) );

		final Img< IntType > imgRoi = ArrayImgs.ints( dims );
		final RandomAccessibleInterval< IntType > viewRoi = Views.interval( imgRoi, Intervals.expand( new FinalInterval( dims ), -border ) );
//		final long[] offset = new long[] { -5, -5 };
//		final IterableInterval< ? extends IterableInterval< IntType > > neighborhoodsRoi = Views.interval(
//				Regions.neighborhoodsRandomAccessible(
//						Views.translate( mask, offset ),
//						viewRoi ),
//				viewRoi );
		final PositionableIterableRegion< BitType > pir = Regions.positionable( mask );
		pir.origin().setPosition( new long[] { 5, 5 } );
		final IterableInterval< ? extends IterableInterval< IntType > > neighborhoodsRoi = Views.interval(
				new TemplateRandomAccessible<>( Regions.sample( pir, viewRoi, true ) ),
				viewRoi );


		for ( final IterableInterval< IntType > s : neighborhoodsRoi )
			for ( final IntType t : s )
				t.set( t.get() + 1 );

		final Img< IntType > img = ArrayImgs.ints( dims );
		final RandomAccessibleInterval< IntType > view = Views.interval( img, Intervals.expand( new FinalInterval( dims ), -border ) );
		final IterableInterval< ? extends IterableInterval< IntType > > neighborhoods = shape.neighborhoods( view );
		for ( final IterableInterval< IntType > s : neighborhoods )
			for ( final IntType t : s )
				t.set( t.get() + 1 );

		final Cursor< ? extends IterableInterval< IntType > > ncRoi = neighborhoodsRoi.cursor();
		final Cursor< ? extends IterableInterval< IntType > > nc = neighborhoods.cursor();

		for ( int i = 0; i < 10; ++i )
		{
			ncRoi.next();
			nc.next();
		}

		ImageJFunctions.show( imgRoi, "imgRoi" );
		ImageJFunctions.show( img, "img" );

		Views.interval( Views.pair( imgRoi, img ), img ).forEach( p -> {
			if ( p.getA().get() != p.getB().get() )
				System.out.println( "oh no!");
		} );
	}

	// Just to run it
	public static void main( final String... args ) throws IOException
	{
		new BenchmarkDebug().init();
	}
}
