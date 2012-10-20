package org.gvsig.gui.beans.incrementabletask;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

public class TestThread {
	public class miThread1 extends Thread {
		public void run() {
			System.out.println("miThread1 begin");
			for (int i = 0; i <= 10; i++) {
				System.out.println(i);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("miThread1 end");
		}
	}

	public TestThread() {
		System.out.println("testThread begin");
		miThread1 a = new miThread1();
		a.start();
		while (true) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("isAlive(): " + a.isAlive());
			if (!a.isAlive())
				break;
		}
		System.out.println("testThread end");
	}

	public static void main(String[] args) {
		System.out.println("main begin");
		new TestThread();
		System.out.println("main end");
	}

}
