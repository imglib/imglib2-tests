package net.imglib2.algorithm.kdtree;

import java.util.ArrayList;
import java.util.Random;

import net.imglib2.KDTree;
import net.imglib2.Point;
import net.imglib2.algorithm.kdtree.ClipConvexPolytopeKDTree;
import net.imglib2.util.BenchmarkHelper;
import net.imglib2.util.LinAlgHelpers;

public class ClipConvexPolytopeKDTreeBenchmark
{
	public static void main( final String[] args )
	{
		final int w = 400;
		final int h = 400;
		final int nPoints = 100000;
		final Random rand = new Random( 123124 );

		final ArrayList< Point > points = new ArrayList< Point >();
		for ( int i = 0; i < nPoints; ++i )
		{
			final int x = rand.nextInt( w );
			final int y = rand.nextInt( h );
			points.add( new Point( x, y ) );
		}

		final double[][] planes = new double[ 5 ][ 3 ]; // unit normal x, y; d

		double[] plane = planes[ 0 ];
		plane[ 0 ] = 1;
		plane[ 1 ] = 1;
		LinAlgHelpers.scale( plane, 1.0 / LinAlgHelpers.length( plane ), plane );
		plane[ 2 ] = 230;

		plane = planes[ 1 ];
		plane[ 0 ] = -1;
		plane[ 1 ] = 1;
		LinAlgHelpers.scale( plane, 1.0 / LinAlgHelpers.length( plane ), plane );
		plane[ 2 ] = -30;

		plane = planes[ 2 ];
		plane[ 0 ] = 0.1;
		plane[ 1 ] = -1;
		LinAlgHelpers.scale( plane, 1.0 / LinAlgHelpers.length( plane ), plane );
		plane[ 2 ] = -230;

		plane = planes[ 3 ];
		plane[ 0 ] = -0.5;
		plane[ 1 ] = -1;
		LinAlgHelpers.scale( plane, 1.0 / LinAlgHelpers.length( plane ), plane );
		plane[ 2 ] = -290;

		plane = planes[ 4 ];
		plane[ 0 ] = -1;
		plane[ 1 ] = 0.1;
		LinAlgHelpers.scale( plane, 1.0 / LinAlgHelpers.length( plane ), plane );
		plane[ 2 ] = -200;

		System.out.println( "partitioning list of points:" );
		BenchmarkHelper.benchmarkAndPrint( 20, false, new Runnable()
		{
			@Override
			public void run()
			{
				for ( int i = 0; i < 500; ++i )
				{
					final ArrayList< Point >[] insideoutside = getInsidePoints( points, planes );
					if ( insideoutside[ 0 ].size() > 1000000 )
						System.out.println( "bla" );
				}
			}
		} );

		System.out.println( "partitioning kdtree of points:" );
		final KDTree< Point > kdtree = new KDTree< Point >( points, points );
		final ClipConvexPolytopeKDTree< Point > clipper = new ClipConvexPolytopeKDTree< Point >( kdtree );
		BenchmarkHelper.benchmarkAndPrint( 20, false, new Runnable()
		{
			@Override
			public void run()
			{
				for ( int i = 0; i < 500; ++i )
				{
					clipper.clip( planes );
				}
			}
		} );
	}

	@SuppressWarnings( "unchecked" )
	static ArrayList< Point >[] getInsidePoints( final ArrayList< Point > points, final double[][] planes )
	{
		final int nPlanes = planes.length;
		final int n = points.get( 0 ).numDimensions();
		final ArrayList< Point > inside = new ArrayList< Point >();
		final ArrayList< Point > outside = new ArrayList< Point >();
		A: for ( final Point p : points )
		{
			for ( int i = 0; i < nPlanes; ++i )
			{
				final double[] plane = planes[ i ];
				double dot = 0;
				for ( int d = 0; d < n; ++d )
					dot += p.getDoublePosition( d ) * plane[ d ];
				if ( dot < plane[ n ] )
				{
					outside.add( p );
					continue A;
				}
			}
			inside.add( p );
		}
		return new ArrayList[] { inside, outside };
	}
}
