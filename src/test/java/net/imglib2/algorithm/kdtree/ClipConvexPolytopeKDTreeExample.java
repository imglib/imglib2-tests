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
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.kdtree.ClipConvexPolytopeKDTree;
import net.imglib2.algorithm.kdtree.ConvexPolytope;
import net.imglib2.algorithm.kdtree.HyperPlane;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.position.transform.Round;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.view.Views;

public class ClipConvexPolytopeKDTreeExample
{
	public static void main( final String[] args )
	{
		final int w = 800;
		final int h = 800;
		final int nPoints = 10000;

		// make random 2D Points
		final Random rand = new Random( 123124 );
		final ArrayList< Point > points = new ArrayList< Point >();
		for ( int i = 0; i < nPoints; ++i )
		{
			final long x = rand.nextInt( w );
			final long y = rand.nextInt( h );
			points.add( new Point( x, y ) );
		}

		// clip on convex polytope
		final ConvexPolytope polytope = new ConvexPolytope(
				new HyperPlane(    1,   1,  460 ),
				new HyperPlane(   -1,   1,  -60 ),
				new HyperPlane(  0.1,  -1, -460 ),
				new HyperPlane( -0.5,  -1, -580 ),
				new HyperPlane(   -1, 0.1, -400 ) );
		final KDTree< Point > kdtree = new KDTree< Point >( points, points );
		final ClipConvexPolytopeKDTree< Point > clip = new ClipConvexPolytopeKDTree< Point >( kdtree );
		clip.clip( polytope );

		// show all points
		final Img< ARGBType > pointsImg = ArrayImgs.argbs( w, h );
		paint( points, pointsImg, new ARGBType( 0x00ff00 ) );
		ImageJFunctions.show( pointsImg );

		// show inside/outside points
		final Img< ARGBType > clipImg = ArrayImgs.argbs( w, h );
		paint( clip.getInsideNodes(), clipImg, new ARGBType( 0xffff00 ) );
		paint( clip.getOutsideNodes(), clipImg, new ARGBType( 0x0000ff ) );
		ImageJFunctions.show( clipImg );
	}

	static void paint( final Iterable< ? extends RealLocalizable > points, final Img< ARGBType > output, final ARGBType color )
	{
		final int radius = 2;
		final RandomAccess< Neighborhood< ARGBType > > na = new HyperSphereShape( radius ).neighborhoodsRandomAccessible( Views.extendZero( output ) ).randomAccess();
		final Round< RandomAccess< Neighborhood< ARGBType > > > rna = new Round< RandomAccess< Neighborhood< ARGBType > > >( na );
		for ( final RealLocalizable l : points )
		{
			rna.setPosition( l );
			for ( final ARGBType t : na.get() )
				t.set( color );
		}
	}
}
