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
package net.imglib2.algorithm.morphology;

import ij.ImageJ;

import io.scif.img.IO;
import io.scif.img.ImgIOException;
import io.scif.img.SCIFIOImgPlus;

import java.io.File;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class BlackTopHatTests
{
	public static void main( final String[] args ) throws ImgIOException
	{
		ImageJ.main( args );
		final File file = new File( "DrosophilaWing.tif" );
		final SCIFIOImgPlus img = IO.openImgs( file.getAbsolutePath() ).get( 0 );
		final Img< UnsignedByteType > imgInv = img.copy();
		final Cursor< UnsignedByteType > cursor = img.cursor();
		final Cursor< UnsignedByteType > cursor2 = imgInv.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor2.fwd();
			cursor2.get().set( 255 - cursor.get().get() );
		}

		final Shape strel = new HyperSphereShape( 5 );

		/*
		 * To new Img
		 */

		ImageJFunctions.show( imgInv, "Source" );

		final Img blackTopHat = BlackTopHat.blackTopHat( imgInv, strel, 1 );
		ImageJFunctions.show( blackTopHat, "BlackTopHatToNewImg" );

		/*
		 * In place
		 */

		final Interval interval = FinalInterval.createMinSize( new long[] { 7, 35, 88, 32 } );

		final Img copy2 = imgInv.copy();
		BlackTopHat.blackTopHatInPlace( copy2, interval, strel, 1 );
		ImageJFunctions.show( copy2, "BlackTopHatInPlace" );

		/*
		 * To target
		 */

		final Img img2 = img.factory().create( interval );
		final long[] translation = new long[ interval.numDimensions() ];
		interval.min( translation );
		final IntervalView translate = Views.translate( img2, translation );
		BlackTopHat.blackTopHat( imgInv, translate, strel, 1 );
		ImageJFunctions.show( img2, "BlackTopHatToTarget" );

	}

}
