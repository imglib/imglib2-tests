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

package net.imglib2.img.sparse;

import ij.ImageJ;

import io.scif.img.IO;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;

public class ExampleQuadtree
{
	final static public void main( final String[] args )
	{
		new ImageJ();

		Img< IntType > array = null;
		final ImgFactory< IntType > arrayFactory = new ArrayImgFactory<>( new IntType() );
		try
		{
			array = IO.openImgs( "/home/tobias/workspace/data/quadtree.tif", arrayFactory ).get( 0 );
		}
		catch ( final Exception e )
		{
			e.printStackTrace();
			return;
		}

		ImageJFunctions.show( array, "array" );

		final NtreeImgFactory< IntType > ntreeFactory = new NtreeImgFactory<>( new IntType() );
		final Img< IntType > quadtree = ntreeFactory.create( array );

		// copy to sparse img
		final Cursor< IntType > dst = quadtree.localizingCursor();
		final RandomAccess< IntType > src = array.randomAccess();
		while( dst.hasNext() )
		{
			dst.fwd();
			src.setPosition( dst );
			dst.get().set( src.get() );
		}
		/*
		final RandomAccess< IntType > dst = quadtree.randomAccess();
		final Cursor< IntType > src = array.localizingCursor();
		while( src.hasNext() )
		{
			src.fwd();
			dst.setPosition( src );
			dst.get().set( src.get() );
		}
		*/

		ImageJFunctions.show( quadtree, "quadtree" );

		System.out.println( "done" );
	}

}
