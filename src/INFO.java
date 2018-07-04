import java.io.*;
import java.util.*;
import java.math.BigInteger;

public class INFO implements Serializable {
	private String name;
	private BigInteger b;
	private int[] intArray;

	public INFO(String name, BigInteger b) {
		this.name = name;
		this.b = b;

		intArray = new int[10];
	}

	public int[] getArray() {
		return intArray;
	}

	public void printInfo() {
		System.out.println("Name: " + name + " Value: " + b);
	}
}
