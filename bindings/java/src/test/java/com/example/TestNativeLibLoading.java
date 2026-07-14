package com.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.cadoodlecad.manifold.ManifoldBindings;

class TestNativeLibLoading {

	@Test
	void test() throws Throwable {
		ManifoldBindings manifold = new ManifoldBindings();
		long seg = manifold.cube(10, 10, 10, false);
		manifold.delete(seg);
	}

}
