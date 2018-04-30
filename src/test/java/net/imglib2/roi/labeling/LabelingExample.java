package net.imglib2.roi.labeling;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

import java.awt.Rectangle;
import java.util.Set;

import net.imglib2.Cursor;
import net.imglib2.Dimensions;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.Regions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

public class LabelingExample
{
	LabelingExample()
	{
		new ij.ImageJ();

		// Create and show a simple test image, 100x100
		final ImagePlus imp = createTestImage();
		imp.show();

		// Create an ImgLabeling from the ImagePlus
		final ImgLabeling< Integer, IntType > labeling = getIntIntImgLabellingFromLabelMapImagePlus( imp );

		// Read out regions and labelNames
		final LabelRegions< Integer > regions = new LabelRegions<>( labeling );
		final Set< Integer > labelNames = labeling.getMapping().getLabels();

		// Visualise the labels as binary images.
		for ( final Integer labelName : labelNames )
		{
			final LabelRegion< Integer > lr = regions.getLabelRegion( labelName );

			System.out.println( "Number of output pixels in region " + labelName + " : " + lr.size() );

			/*
			 * Use the LabelRegion as a RandomAccessible< BoolType >, put the
			 * bounds of the image on it, and display the result.
			 */
			ImageJFunctions.show( Views.interval( lr, labeling ), "region " + labelName + " (view)" );

			/*
			 * Use LabelRegion as a IterableInterval< Void >, bind it to a new
			 * BitType Img, set the region pixels to true, and display the
			 * result.
			 */
			final Img< BitType > bits = ArrayImgs.bits( Intervals.dimensionsAsLongArray( labeling ) );
			Regions.sample( lr, bits ).forEach( t -> t.set( true ) );
			ImageJFunctions.show( bits, "region " + labelName + " (copied bitmask)" );
		}
	}

	/*
	 * Create an image with three labels (+background) The three labels have an
	 * area of 100, 200 and 300.
	 */
	private ImagePlus createTestImage()
	{
		final ImagePlus imp = NewImage.createByteImage( "Test image", 100, 100, 1, NewImage.FILL_BLACK );
		final ImageProcessor ip = imp.getProcessor();
		ip.setRoi( new Rectangle( 10, 10, 10, 10 ) );
		ip.setColor( 1 );
		ip.fill();
		ip.setRoi( new Rectangle( 20, 20, 10, 20 ) );
		ip.setColor( 2 );
		ip.fill();
		ip.setRoi( new Rectangle( 40, 40, 10, 30 ) );
		ip.setColor( 3 );
		ip.fill();

		return imp;
	}

	/*
	 * Transform an ImagePlus to a Labeling Imp. The original pixel value should
	 * be the label afterwards.
	 */
	public static ImgLabeling< Integer, IntType > getIntIntImgLabellingFromLabelMapImagePlus( final ImagePlus labelMap )
	{
		final Img< FloatType > img2 = ImageJFunctions.convertFloat( labelMap );

		final Dimensions dims = img2;
		final IntType t = new IntType();
		final RandomAccessibleInterval< IntType > img = Util.getArrayOrCellImgFactory( dims, t ).create( dims );
		final ImgLabeling< Integer, IntType > labeling = new ImgLabeling<>( img );

		final Cursor< LabelingType< Integer > > labelCursor = Views.flatIterable( labeling ).cursor();
		for ( final UnsignedByteType input : Views.flatIterable( ImageJFunctions.wrapByte( labelMap ) ) )
		{
			final LabelingType< Integer > element = labelCursor.next();
			if ( input.get() != 0 )
				element.add( input.get() );
		}

		return labeling;
	}

	// Just to run it
	public static void main( final String... args )
	{
		new LabelingExample();
	}
}
