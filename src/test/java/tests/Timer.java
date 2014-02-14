/*
 * #%L
 * Integration tests for ImgLib2.
 * %%
 * Copyright (C) 2011 - 2014 SciJava
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
package tests;

/**
 * Clean room implementation of the Timer used in ImgLib's test classes.
 * 
 * This is a clean-room, CC0-licensed (no matter what this file's license
 * header says) version of the timer.
 * 
 * @author Johannes Schindelin
 */
public class Timer {
	private long start;

	public void start() {
		start = System.currentTimeMillis();
	}

	public long stop() {
		return System.currentTimeMillis() - start;
	}
}
