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
package net.imglib2.algorithm.labeling;

import static net.imglib2.algorithm.labeling.ConnectedComponents.StructuringElement.FOUR_CONNECTED;
import ij.ImageJ;
import io.scif.img.IO;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelingMapping;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

public class ConnectedComponentsExample
{
	public static void main( final String[] args )
	{
		ImageJ.main( args );

		final String fn = "/Users/pietzsch/Desktop/data/cca2.tif";
		final RandomAccessibleInterval< UnsignedByteType > img = IO.openImgs( fn, new ArrayImgFactory< UnsignedByteType >(), new UnsignedByteType() ).get( 0 ).getImg();

		final long[] dims = new long[ img.numDimensions() ];
		img.dimensions( dims );
		final RandomAccessibleInterval< UnsignedShortType > indexImg = ArrayImgs.unsignedShorts( dims );
		final ImgLabeling< Integer, UnsignedShortType > labeling = new ImgLabeling< Integer, UnsignedShortType >( indexImg );
		final Iterator< Integer > labels = new Iterator< Integer >()
		{
			private int i = 1;

			@Override
			public boolean hasNext()
			{
				return true;
			}

			@Override
			public Integer next()
			{
				return i++;
			}

			@Override
			public void remove()
			{}
		};

		ConnectedComponents.labelAllConnectedComponents( img, labeling, labels, FOUR_CONNECTED );

		final Img< ARGBType > argb = ArrayImgs.argbs( dims );
		colorLabels( labeling, new ColorStream().iterator(), argb );
		ImageJFunctions.show( argb );
	}

	private static < C extends Type< C >, L > void colorLabels(
			final ImgLabeling< L, ? > labeling,
			final Iterator< C > colors,
			final RandomAccessibleInterval< C > output )
	{
		final HashMap< Set< ? >, C > colorTable = new HashMap< Set< ? >, C >();
		final LabelingMapping< ? > mapping = labeling.getMapping();
		final int numLists = mapping.numSets();
		final C color = Util.getTypeFromInterval( output ).createVariable();
		colorTable.put( mapping.labelsAtIndex( 0 ), color );
		for ( int i = 1; i < numLists; ++i )
		{
			final Set< ? > list = mapping.labelsAtIndex( i );
			colorTable.put( list, colors.next() );
		}
		final Iterator< C > o = Views.flatIterable( output ).iterator();
		for ( final C c : Converters.convert(
				Views.flatIterable( labeling ),
				new LabelingTypeConverter< C >( colorTable ),
				color ) )
			o.next().set( c );
	}

	private static final class LabelingTypeConverter< T extends Type< T > > implements Converter< LabelingType< ? >, T >
	{
		private final HashMap< Set< ? >, T > colorTable;

		public LabelingTypeConverter( final HashMap< Set< ? >, T > colorTable )
		{
			this.colorTable = colorTable;
		}

		@Override
		public void convert( final LabelingType< ? > input, final T output )
		{
			final T t = colorTable.get( input );
			if ( t != null )
				output.set( t );
		}
	}

	private static final class ColorStream implements Iterable< ARGBType >
	{
		final static protected double goldenRatio = 0.5 * Math.sqrt( 5 ) + 0.5;

		final static protected double stepSize = 6.0 * goldenRatio;

		final static protected double[] rs = new double[] { 1, 1, 0, 0, 0, 1, 1 };

		final static protected double[] gs = new double[] { 0, 1, 1, 1, 0, 0, 0 };

		final static protected double[] bs = new double[] { 0, 0, 0, 1, 1, 1, 0 };

		final static protected int interpolate( final double[] xs, final int k, final int l, final double u, final double v )
		{
			return ( int ) ( ( v * xs[ k ] + u * xs[ l ] ) * 255.0 + 0.5 );
		}

		final static protected int argb( final int r, final int g, final int b )
		{
			return ( ( ( r << 8 ) | g ) << 8 ) | b | 0xff000000;
		}

		final static int get( final long index )
		{
			double x = goldenRatio * index;
			x -= ( long ) x;
			x *= 6.0;
			final int k = ( int ) x;
			final int l = k + 1;
			final double u = x - k;
			final double v = 1.0 - u;

			final int r = interpolate( rs, k, l, u, v );
			final int g = interpolate( gs, k, l, u, v );
			final int b = interpolate( bs, k, l, u, v );

			return argb( r, g, b );
		}

		@Override
		final public Iterator< ARGBType > iterator()
		{
			return new Iterator< ARGBType >()
			{
				long i = -1;

				@Override
				public boolean hasNext()
				{
					return true;
				}

				@Override
				public ARGBType next()
				{
					return new ARGBType( get( ++i ) );
				}

				@Override
				public void remove()
				{}
			};
		}
	}
}
