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

package net.imglib2.algorithm.gauss3;

import io.scif.img.IO;
import io.scif.img.ImgIOException;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * Example for using {@link Gauss3}.
 *
 * @author Tobias Pietzsch
 */
public class Gauss3Example
{
	public static void main( final String[] args ) throws ImgIOException
	{
		final String fn = "/home/tobias/workspace/data/DrosophilaWing.tif";
		final Img< FloatType > img = IO.openImgs( fn, new ArrayImgFactory<>( new FloatType() ) ).get( 0 );

		final long[] dims = new long[ img.numDimensions() ];
		img.dimensions( dims );
		final Img< FloatType > convolved = ArrayImgs.floats( dims );

		try
		{
			Gauss3.gauss( 3, Views.extendMirrorSingle( img ), convolved );
//			Gauss3.gauss( 5, img, Views.interval( convolved, Intervals.createMinSize( 200, 100, 200, 150 ) ) );
		}
		catch ( final IncompatibleTypeException e )
		{
			e.printStackTrace();
		}
		ImageJFunctions.show( convolved );
	}
}
