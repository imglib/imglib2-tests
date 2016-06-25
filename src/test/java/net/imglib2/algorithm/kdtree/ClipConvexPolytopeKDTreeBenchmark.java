/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
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
			final long x = rand.nextInt( w );
			final long y = rand.nextInt( h );
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
