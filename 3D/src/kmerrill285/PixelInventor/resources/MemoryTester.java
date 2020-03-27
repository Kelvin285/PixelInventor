package kmerrill285.PixelInventor.resources;

public class MemoryTester {
	public static void queryMemory() {
		long heapSize = Runtime.getRuntime().totalMemory(); 

		long heapMaxSize = Runtime.getRuntime().maxMemory();

		long heapFreeSize = Runtime.getRuntime().freeMemory(); 
		System.out.println("Current heap size: " + heapSize);
		System.out.println("Max heap size: " + heapMaxSize);
		System.out.println("Free heep size: " + heapFreeSize);
	}
}
