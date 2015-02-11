/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

package net.imglib2.view;

import ij.ImageJ;
import io.scif.img.ImgOpener;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 */
public class OpenAndDisplayView
{
	final static public void main( final String[] args )
	{
		new ImageJ();
		
		Img< FloatType > img = null;
		try
		{
			ImgFactory< FloatType > imgFactory = new ArrayImgFactory< FloatType >();
			final ImgOpener io = new ImgOpener();
			img = io.openImg( "/home/tobias/workspace/data/wingclip.tif", imgFactory, new FloatType() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return;
		}

		
		RandomAccessible< FloatType >         view1 = Views.extendMirrorSingle( img );	
		RandomAccessibleInterval< FloatType > view2 = Views.offsetInterval( view1, new long[] {-20, -20}, new long[] {157, 157} );		
		RandomAccessible< FloatType >         view3 = Views.extendPeriodic( view2 );	
		RandomAccessibleInterval< FloatType > view4 = Views.offsetInterval( view3, new long[] {-100, -100}, new long[] {357, 357} );		
		RandomAccessibleInterval< FloatType > view5 = Views.offsetInterval( view4, new long[] {120, 120}, new long[] {117, 117} );		
		
		RandomAccessibleInterval< FloatType > finalView = view5;

		ImageJFunctions.show( finalView );
	}
}
