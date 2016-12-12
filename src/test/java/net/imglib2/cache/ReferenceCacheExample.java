/**
 *
 */
package net.imglib2.cache;

import java.util.Arrays;

/**
 * @author Stephan Saalfeld
 *
 */
public class ReferenceCacheExample
{
	final static public void main( String... args ) throws InterruptedException
	{
		final CacheReferenceQueue< Long, ? super byte[] > referenceQueue = new CacheReferenceQueue<>();
		final CacheReferenceQueueCleanupThread cleanupThread  = new CacheReferenceQueueCleanupThread( referenceQueue );
		cleanupThread.start();

		final Loader< Long,  byte[] > loader = new Loader< Long, byte[] >()
		{
			@Override
			public byte[] get( final Long key )
			{
				final byte[] bytes = new byte[ 20000 ];
				Arrays.fill( bytes, ( byte )key.longValue() );
				return bytes;
			}
		};

		final SoftReferenceCache< Long, byte[] > cache = new SoftReferenceCache< Long, byte[] >( loader, referenceQueue );

		long key = 0;

		while ( true )
		{
			final byte[] cell = cache.get( ++key );
			synchronized ( Thread.currentThread() )
			{
				Thread.currentThread().wait( 2 );
			}
		}
	}
}
