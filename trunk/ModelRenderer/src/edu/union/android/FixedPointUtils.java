package edu.union.android;

// Much of this is adapted from the beartronics FP lib
public class FixedPointUtils {
	protected static int toFixed(float val) {
		return (int)(val * 65536F);
	}

	protected static int[] toFixed(float[] arr) {
		int[] res = new int[arr.length];
		for (int i=0;i<res.length;i++) {
			res[i] = toFixed(arr[i]);
		}
		return res;
	}
	
	protected static float toFloat(int val) {
		return ((float)val)/65536.0f;
	}
	
	protected static float[] toFloat(int[] arr) {
		float[] res = new float[arr.length];
		for (int i=0;i<res.length;i++) {
			res[i] = toFloat(arr[i]);
		}
		return res;
	}
	
	public static int multiply (int x, int y) {
		long z = (long) x * (long) y;
		return ((int) (z >> 16));
	}

	public static int divide (int x, int y) {
		long z = (((long) x) << 32);
		return (int) ((z / y) >> 16);
	}	
	
	 public static int sqrt (int n) {
		int s = (n + 65536) >> 1;
		for (int i = 0; i < 8; i++) {
			//converge six times
			s = (s + divide(n, s)) >> 1;
		}
		return s;
	 }
}
