package com.cadoodlecad.manifold;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.util.zip.Deflater;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ManifoldBindings implements AutoCloseable {
	public enum ManifoldError {
		NO_ERROR, NON_FINITE_VERTEX, NOT_MANIFOLD, VERTEX_INDEX_OUT_OF_BOUNDS, PROPERTIES_WRONG_LENGTH,
		MISSING_POSITION_PROPERTIES, MERGE_VECTORS_DIFFERENT_LENGTHS, MERGE_INDEX_OUT_OF_BOUNDS, TRANSFORM_WRONG_LENGTH,
		RUN_INDEX_WRONG_LENGTH, FACE_ID_WRONG_LENGTH, INVALID_CONSTRUCTION, RESULT_TOO_LARGE;

		public static ManifoldError fromInt(int code) {
			ManifoldError[] values = values();
			if (code < 0 || code >= values.length)
				throw new IllegalArgumentException("Unknown ManifoldError code: " + code);
			return values[code];
		}
	}

	public enum FillRule {
		EVEN_ODD, NON_ZERO, POSITIVE, NEGATIVE;

		public int toInt() {
			return ordinal();
		}
	}

	public enum JoinType {
		SQUARE, ROUND, MITER;

		public int toInt() {
			return ordinal();
		}
	}

	public static final int OPTYPE_UNION = 0;
	public static final int OPTYPE_DIFFERENCE = 1;
	public static final int OPTYPE_INTERSECTION = 2;

	private static boolean loaded = false;

	private static native void nativeInit();
	private static native long jniAlloc(long size);
	private static native void jniFree(long ptr);

	// Allocation
	private static native long jniAllocManifold();
	private static native long jniAllocMeshGL64();
	private static native long jniAllocManifoldVec();
	private static native long jniAllocBox();
	private static native long jniAllocPolygons();
	private static native long jniAllocCrossSection();

	// Construction
	private static native long jniOfMeshGL64(long mem, long mesh);
	private static native long jniCopy(long mem, long m);
	private static native long jniEmpty(long mem);
	private static native long jniTetrahedron(long mem);
	private static native long jniCube(long mem, double x, double y, double z, int center);
	private static native long jniSphere(long mem, double radius, int circularSegments);
	private static native long jniCylinder(long mem, double height, double radiusLow, double radiusHigh, int circularSegments, int center);

	// Boolean
	private static native long jniBoolean(long mem, long a, long b, int op);
	private static native long jniUnion(long mem, long a, long b);
	private static native long jniDifference(long mem, long a, long b);
	private static native long jniIntersection(long mem, long a, long b);
	private static native long jniMinkowskiSum(long mem, long a, long b);
	private static native long jniMinkowskiDifference(long mem, long a, long b);
	private static native long jniBatchBoolean(long mem, long ms, int op);

	// Vec
	private static native void jniManifoldVecPushBack(long ms, long m);

	// Transforms
	private static native long jniTransform(long mem, long m, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4);
	private static native long jniTranslate(long mem, long m, double x, double y, double z);
	private static native long jniScale(long mem, long m, double x, double y, double z);
	private static native long jniMirror(long mem, long m, double nx, double ny, double nz);
	private static native long jniRotate(long mem, long m, double x, double y, double z);

	// Refinement
	private static native long jniRefine(long mem, long m, int refine);
	private static native long jniRefineToLength(long mem, long m, double length);
	private static native long jniRefineToTolerance(long mem, long m, double tolerance);
	private static native long jniSimplify(long mem, long m, double tolerance);
	private static native long jniSmoothByNormals(long mem, long m, int normalIdx);
	private static native long jniCalculateNormals(long mem, long m, int normalIdx, double minSharpAngle);
	private static native long jniSmoothOut(long mem, long m, double minSharpAngle, double minSmoothness);

	// Split/Trim
	private static native long[] jniSplit(long a, long b);
	private static native long jniTrimByPlane(long mem, long m, double nx, double ny, double nz, double offset);
	private static native long[] jniSplitByPlane(long m, double nx, double ny, double nz, double offset);

	// Slice
	private static native long jniSlice(long mem, long m, double height);

	// Polygons
	private static native long jniPolygonsLength(long ps);
	private static native long jniPolygonsSimpleLength(long ps, long idx);
	private static native double[] jniPolygonsGetPoint(long ps, long simpleIdx, long ptIdx);

	// Analysis
	private static native double jniVolume(long m);
	private static native double jniSurfaceArea(long m);
	private static native long jniBoundingBox(long mem, long m);
	private static native double[] jniBoxMin(long box);
	private static native double[] jniBoxMax(long box);
	private static native double[] jniBoxCenter(long box);
	private static native double[] jniBoxDimensions(long box);
	private static native double jniEpsilon(long m);
	private static native int jniGenus(long m);

	// Composition
	private static native long jniCompose(long mem, long ms);
	private static native long jniDecompose(long mem, long m);
	private static native long jniAsOriginal(long mem, long m);

	// Hull
	private static native long jniHull(long mem, long m);
	private static native long jniHullPts(long mem, double[] pts, long count);
	private static native long jniBatchHull(long mem, long ms);

	// Info
	private static native long jniManifoldSize();
	private static native long jniMeshGL64Size();
	private static native long jniNumEdge(long m);
	private static native long jniNumProp(long m);
	private static native long jniNumVert(long m);
	private static native long jniNumTri(long m);
	private static native int jniStatus(long m);
	private static native int jniIsEmpty(long m);
	private static native long jniManifoldVecSize();
	private static native long jniManifoldVecLength(long ms);
	private static native long jniManifoldVecGet(long mem, long ms, long idx);

	// MeshGL64 export
	private static native long jniGetMeshGL64(long mem, long m);
	private static native long jniMeshGL64NumVert(long m);
	private static native long jniMeshGL64NumTri(long m);
	private static native long jniMeshGL64NumProp(long m);
	private static native long jniMeshGL64VertPropertiesLength(long m);
	private static native long jniMeshGL64TriLength(long m);
	private static native double[] jniMeshGL64VertProperties(long m);
	private static native long[] jniMeshGL64TriVerts(long m);
	private static native long jniMeshGL64Merge(long mem, long m);
	private static native long jniMeshGL64(long mem, double[] vertProps, long nVerts, long nProps, long[] triVerts, long nTris);

	// Cleanup
	private static native void jniDeleteManifold(long m);
	private static native void jniDeleteMeshGL64(long m);
	private static native void jniDeleteManifoldVec(long ms);
	private static native void jniDeleteBox(long b);
	private static native void jniDeletePolygons(long p);
	private static native void jniDeleteCrossSection(long cs);

	// Quality globals
	private static native void jniSetCircularSegments(int number);
	private static native void jniSetMinCircularAngle(double degrees);
	private static native int jniGetCircularSegments(double radius);

	// CrossSection
	private static native long jniAllocCrossSectionNative();
	private static native long jniCrossSectionSize();
	private static native long jniCrossSectionOfPolygons(long mem, long ps, int fr);
	private static native long jniCrossSectionOffset(long mem, long cs, double delta, int jt, double miterLimit, int circularSegments);
	private static native long jniCrossSectionToPolygons(long mem, long cs);

	// ============================================================
	// Library loading
	// ============================================================

	private static void loadNativeLibrary(String libName, File cacheDirectory) throws Exception {
		loadNativeLibrary(libName, cacheDirectory, null);
	}

	private static void loadNativeLibrary(String libName, File cacheDirectory, String linkName) throws Exception {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			String arch = System.getProperty("os.arch").toLowerCase();
			if (arch.equals("amd64"))
				arch = "x86_64";
			if (arch.contains("aarch"))
				arch = "arm64";
			String platform;
			String extension;
			if (os.contains("win")) {
				platform = "win-" + arch;
				extension = ".dll";
				if (libName.startsWith("lib")) {
					libName = libName.substring(3);
				}
			} else if (os.contains("mac")) {
				platform = "mac-" + arch;
				extension = ".dylib";
			} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
				platform = "linux-" + arch;
				extension = ".so";
			} else {
				throw new RuntimeException("Unsupported OS: " + os);
			}

			String fullName = libName + extension;
			File libsDir = cacheDirectory != null ? cacheDirectory : Files.createTempDirectory("manifold3d").toFile();
			if (cacheDirectory == null)
				libsDir.deleteOnExit();
			if (!libsDir.exists())
				libsDir.mkdirs();

			File libFile = new File(libsDir, fullName);
			if (!libFile.exists()) {
				try (java.io.InputStream in = ManifoldBindings.class.getResourceAsStream("/manifold3d/natives/" + platform + "/" + fullName)) {
					if (in == null)
						throw new RuntimeException("Library not found: " + fullName + " for platform " + platform);
					Files.copy(in, libFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
					System.out.println("Extracted to libs/: " + fullName);
					if (cacheDirectory == null)
						libFile.deleteOnExit();
				}
			} else {
				System.out.println("Copy not performed, already in cache");
			}

			if (linkName != null) {
				File linkFile = new File(libsDir, linkName);
				if (!linkFile.exists()) {
					java.nio.file.Files.createSymbolicLink(linkFile.toPath(), libFile.toPath());
					System.out.println("Created symlink: " + linkName + " -> " + fullName);
				}
				return;
			}

			System.out.println("Loading library " + libFile.getAbsolutePath());
			System.load(libFile.getAbsolutePath());
		} catch (Exception e) {
			throw new RuntimeException("Failed to load: " + libName, e);
		}
	}

	private static void loadNativeLibraries(File cacheDirectory) throws Exception {
		File dir = cacheDirectory != null ? cacheDirectory : Files.createTempDirectory("manifold3d").toFile();
		if (cacheDirectory == null) {
			dir.deleteOnExit();
		}
		if (!dir.exists())
			dir.mkdirs();
		loadNativeLibrary("libmanifold", dir);
		loadNativeLibrary("libmanifold.so.3", dir, "libmanifold.so");
		loadNativeLibrary("libmanifoldc", dir);
		loadNativeLibrary("libmanifoldc.so.3", dir, "libmanifoldc.so");
		loadNativeLibrary("libmanifold_jni", dir);
		nativeInit();
		loaded = true;
	}

	// ============================================================
	// Constructor
	// ============================================================

	public ManifoldBindings() throws Exception {
		this((File) null);
	}

	public ManifoldBindings(File cacheDirectory) throws Exception {
		if (!isNativeLibraryLoaded()) {
			loadNativeLibraries(cacheDirectory);
		}
	}

	// ============================================================
	// Static Quality Globals
	// ============================================================

	public void setCircularSegments(int segments) {
		jniSetCircularSegments(segments);
	}

	public void setMinCircularAngle(double degrees) {
		jniSetMinCircularAngle(degrees);
	}

	public int getCircularSegments(double radius) {
		return jniGetCircularSegments(radius);
	}

	// ============================================================
	// Status
	// ============================================================

	public ManifoldError status(long m) {
		int code = jniStatus(m);
		return ManifoldError.fromInt(code);
	}

	// ============================================================
	// Primitives
	// ============================================================

	public long empty() {
		long mem = jniAllocManifold();
		return jniEmpty(mem);
	}

	public long tetrahedron() {
		long mem = jniAllocManifold();
		return jniTetrahedron(mem);
	}

	public long cube(double x, double y, double z, boolean center) {
		long mem = jniAllocManifold();
		return jniCube(mem, x, y, z, center ? 1 : 0);
	}

	public long sphere(double radius, int segments) {
		long mem = jniAllocManifold();
		return jniSphere(mem, radius, segments);
	}

	public long cylinder(double height, double radiusLow, double radiusHigh, int segments, int center) {
		long mem = jniAllocManifold();
		return jniCylinder(mem, height, radiusLow, radiusHigh, segments, center);
	}

	// ============================================================
	// Transformations
	// ============================================================

	public long transform(long m, double x1, double y1, double z1, double x2, double y2, double z2,
			double x3, double y3, double z3, double x4, double y4, double z4) {
		long mem = jniAllocManifold();
		return jniTransform(mem, m, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);
	}

	public long translate(long m, double x, double y, double z) {
		long mem = jniAllocManifold();
		return jniTranslate(mem, m, x, y, z);
	}

	public long scale(long m, double x, double y, double z) {
		long mem = jniAllocManifold();
		return jniScale(mem, m, x, y, z);
	}

	public long rotate(long m, double x, double y, double z) {
		long mem = jniAllocManifold();
		return jniRotate(mem, m, x, y, z);
	}

	public long rotateX(long m, double degrees) {
		return rotate(m, degrees, 0, 0);
	}

	public long rotateY(long m, double degrees) {
		return rotate(m, 0, degrees, 0);
	}

	public long rotateZ(long m, double degrees) {
		return rotate(m, 0, 0, degrees);
	}

	public long mirror(long m, double nx, double ny, double nz) {
		long mem = jniAllocManifold();
		return jniMirror(mem, m, nx, ny, nz);
	}

	// ============================================================
	// Boolean operations
	// ============================================================

	public long union(long a, long b) {
		long mem = jniAllocManifold();
		return jniUnion(mem, a, b);
	}

	public long difference(long a, long b) {
		long mem = jniAllocManifold();
		return jniDifference(mem, a, b);
	}

	public long intersection(long a, long b) {
		long mem = jniAllocManifold();
		return jniIntersection(mem, a, b);
	}

	public long minkowskiSum(long a, long b) {
		long mem = jniAllocManifold();
		return jniMinkowskiSum(mem, a, b);
	}

	public long minkowskiDifference(long a, long b) {
		long mem = jniAllocManifold();
		return jniMinkowskiDifference(mem, a, b);
	}

	public long booleanOp(long a, long b, int opType) {
		long mem = jniAllocManifold();
		return jniBoolean(mem, a, b, opType);
	}

	public long batchUnion(long[] shapes) {
		long vec = jniAllocManifoldVec();
		try {
			for (long shape : shapes)
				jniManifoldVecPushBack(vec, shape);
			long resultMem = jniAllocManifold();
			return jniBatchBoolean(resultMem, vec, OPTYPE_UNION);
		} finally {
			jniDeleteManifoldVec(vec);
		}
	}

	// ============================================================
	// Refinement
	// ============================================================

	public long refine(long m, int level) {
		long mem = jniAllocManifold();
		return jniRefine(mem, m, level);
	}

	public long refineToLength(long m, double length) {
		long mem = jniAllocManifold();
		return jniRefineToLength(mem, m, length);
	}

	public long refineToTolerance(long m, double tolerance) {
		long mem = jniAllocManifold();
		return jniRefineToTolerance(mem, m, tolerance);
	}

	public long simplify(long m, double tolerance) {
		long mem = jniAllocManifold();
		return jniSimplify(mem, m, tolerance);
	}

	public long smoothByNormals(long m, int normalIdx) {
		long mem = jniAllocManifold();
		return jniSmoothByNormals(mem, m, normalIdx);
	}

	public long calculateNormals(long m, int normalIdx, double minSharpAngle) {
		long mem = jniAllocManifold();
		return jniCalculateNormals(mem, m, normalIdx, minSharpAngle);
	}

	public long smoothOut(long m, double minSharpAngle, double minSmoothness) {
		long mem = jniAllocManifold();
		return jniSmoothOut(mem, m, minSharpAngle, minSmoothness);
	}

	// ============================================================
	// Split/Trim
	// ============================================================

	public long trimByPlane(long m, double nx, double ny, double nz, double offset) {
		long mem = jniAllocManifold();
		return jniTrimByPlane(mem, m, nx, ny, nz, offset);
	}

	public long[] split(long a, long b) {
		return jniSplit(a, b);
	}

	public long[] splitByPlane(long m, double nx, double ny, double nz, double offset) {
		return jniSplitByPlane(m, nx, ny, nz, offset);
	}

	// ============================================================
	// Slice
	// ============================================================

	public ArrayList<double[][]> slice(long m, double height) {
		long polygons = jniSlice(jniAllocPolygons(), m, height);
		try {
			long numContours = jniPolygonsLength(polygons);
			if (numContours == 0)
				return new ArrayList<>();
			ArrayList<double[][]> result = new ArrayList<>((int) numContours);
			for (int c = 0; c < numContours; c++) {
				long len = jniPolygonsSimpleLength(polygons, c);
				double[][] contour = new double[(int) len][2];
				for (int i = 0; i < len; i++) {
					double[] pt = jniPolygonsGetPoint(polygons, c, i);
					contour[i][0] = pt[0];
					contour[i][1] = pt[1];
				}
				result.add(contour);
			}
			return result;
		} finally {
			jniDeletePolygons(polygons);
		}
	}

	public ArrayList<double[][]> sliceWithOffset(long m, double height, double delta, JoinType joinType,
			double miterLimit, int circularSegs) {
		long slicePolys = jniSlice(jniAllocPolygons(), m, height);
		long section = jniCrossSectionOfPolygons(jniAllocCrossSectionNative(), slicePolys, FillRule.POSITIVE.toInt());
		long offsetSec = jniCrossSectionOffset(jniAllocCrossSectionNative(), section, delta,
				joinType.toInt(), miterLimit, circularSegs);
		long offsetPolys = jniCrossSectionToPolygons(jniAllocPolygons(), offsetSec);
		try {
			return readPolygons(offsetPolys);
		} finally {
			jniDeletePolygons(slicePolys);
			jniDeleteCrossSection(section);
			jniDeleteCrossSection(offsetSec);
			jniDeletePolygons(offsetPolys);
		}
	}

	private ArrayList<double[][]> readPolygons(long polygons) {
		long numContours = jniPolygonsLength(polygons);
		if (numContours == 0)
			return new ArrayList<>();
		ArrayList<double[][]> result = new ArrayList<>((int) numContours);
		for (int c = 0; c < numContours; c++) {
			long len = jniPolygonsSimpleLength(polygons, c);
			double[][] contour = new double[(int) len][2];
			for (int i = 0; i < len; i++) {
				double[] pt = jniPolygonsGetPoint(polygons, c, i);
				contour[i][0] = pt[0];
				contour[i][1] = pt[1];
			}
			result.add(contour);
		}
		return result;
	}

	// ============================================================
	// Hull
	// ============================================================

	public long hull(long m) {
		long mem = jniAllocManifold();
		return jniHull(mem, m);
	}

	public long hull(ArrayList<double[]> points) {
		if (points == null || points.isEmpty())
			return empty();
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i) == null || points.get(i).length != 3)
				throw new IllegalArgumentException("Point at index " + i + " must be a double[3]");
		}
		double[] flat = new double[points.size() * 3];
		for (int i = 0; i < points.size(); i++) {
			flat[i * 3] = points.get(i)[0];
			flat[i * 3 + 1] = points.get(i)[1];
			flat[i * 3 + 2] = points.get(i)[2];
		}
		long mem = jniAllocManifold();
		return jniHullPts(mem, flat, points.size());
	}

	public long batchHull(long[] shapes) {
		long vec = jniAllocManifoldVec();
		try {
			for (long shape : shapes)
				jniManifoldVecPushBack(vec, shape);
			long mem = jniAllocManifold();
			return jniBatchHull(mem, vec);
		} finally {
			jniDeleteManifoldVec(vec);
		}
	}

	// ============================================================
	// Compose/Decompose
	// ============================================================

	public long compose(long[] parts) {
		long vec = jniAllocManifoldVec();
		try {
			for (long part : parts)
				jniManifoldVecPushBack(vec, part);
			long mem = jniAllocManifold();
			return jniCompose(mem, vec);
		} finally {
			jniDeleteManifoldVec(vec);
		}
	}

	// ============================================================
	// Copy / AsOriginal
	// ============================================================

	public long copy(long m) {
		long mem = jniAllocManifold();
		return jniCopy(mem, m);
	}

	public long asOriginal(long m) {
		long mem = jniAllocManifold();
		return jniAsOriginal(mem, m);
	}

	// ============================================================
	// Bounding box
	// ============================================================

	public long boundingBox(long m) {
		long box = jniAllocBox();
		return jniBoundingBox(box, m);
	}

	public double[] boxDimensions(long box) {
		return jniBoxDimensions(box);
	}

	public BoundingBox3D getJavaFXBounds(long m) {
		long box = jniAllocBox();
		try {
			jniBoundingBox(box, m);
			double[] min = jniBoxMin(box);
			double[] max = jniBoxMax(box);
			return new BoundingBox3D(min[0], min[1], min[2], max[0] - min[0], max[1] - min[1], max[2] - min[2]);
		} finally {
			jniDeleteBox(box);
		}
	}

	public BoundingBox3D getBounds(long m) {
		long box = jniAllocBox();
		try {
			jniBoundingBox(box, m);
			double[] dims = jniBoxDimensions(box);
			return new BoundingBox3D(0, 0, 0, dims[0], dims[1], dims[2]);
		} finally {
			jniDeleteBox(box);
		}
	}

	public long centerObject(long m) {
		long box = jniAllocBox();
		try {
			jniBoundingBox(box, m);
			double[] center = jniBoxCenter(box);
			return translate(m, -center[0], -center[1], -center[2]);
		} finally {
			jniDeleteBox(box);
		}
	}

	// ============================================================
	// Info
	// ============================================================

	public long manifoldSize() {
		return jniManifoldSize();
	}

	public long meshGL64Size() {
		return jniMeshGL64Size();
	}

	public long vecSize() {
		return jniManifoldVecSize();
	}

	public double volume(long m) {
		return jniVolume(m);
	}

	public double surfaceArea(long m) {
		return jniSurfaceArea(m);
	}

	public double epsilon(long m) {
		return jniEpsilon(m);
	}

	public int genus(long m) {
		return jniGenus(m);
	}

	public long numVert(long m) {
		return jniNumVert(m);
	}

	public long numTri(long m) {
		return jniNumTri(m);
	}

	public long numEdge(long m) {
		return jniNumEdge(m);
	}

	public long numProp(long m) {
		return jniNumProp(m);
	}

	public boolean isEmpty(long m) {
		return jniIsEmpty(m) != 0;
	}

	public long estimateMemoryBytes(long m) {
		long nVert = numVert(m);
		long nTri = numTri(m);
		long nEdge = numEdge(m);
		long nProp = numProp(m);

		long vertexData = nVert * 24L;
		long halfedgeData = nEdge * 40L;
		long faceData = nTri * 8L;
		long edgeData = nEdge * 8L;
		long propertyData = nVert * nProp * 8L;

		long meshglVertexData = nVert * Math.max(3, nProp) * 8L;
		long meshglIndexData = nTri * 3L * 8L;
		long meshglStruct = meshGL64Size();

		long internalTotal = vertexData + halfedgeData + faceData + edgeData + propertyData;
		long exportTotal = meshglStruct + meshglVertexData + meshglIndexData;
		long manifoldHandle = manifoldSize();
		long bvhOverhead = (long) (internalTotal * 0.25);

		return manifoldHandle + internalTotal + exportTotal + bvhOverhead;
	}

	// ============================================================
	// Mesh import/export (64-bit)
	// ============================================================

	public long importMeshGL64(double[] vertices, long[] triangles, long nVerts, long nTris) {
		long meshGLMem = jniAllocMeshGL64();
		long meshGL = jniMeshGL64(meshGLMem, vertices, nVerts, 3L, triangles, nTris);
		long mergedMem = jniAllocMeshGL64();
		long merged = jniMeshGL64Merge(mergedMem, meshGL);
		long manMem = jniAllocManifold();
		long result = jniOfMeshGL64(manMem, merged);
		if (merged != result)
			jniDeleteMeshGL64(merged);
		if (meshGL != result)
			jniDeleteMeshGL64(meshGL);
		return result;
	}

	public MeshData64 exportMeshGL64(long manifold) {
		long meshGLMem = jniAllocMeshGL64();
		long meshGL = jniGetMeshGL64(meshGLMem, manifold);
		try {
			long numVert = jniMeshGL64NumVert(meshGL);
			long numTri = jniMeshGL64NumTri(meshGL);
			long numProp = jniMeshGL64NumProp(meshGL);

			double[] vertices = new double[(int) (numVert * 3)];
			long[] triangles = new long[(int) (numTri * 3)];

			if (numVert > 0) {
				double[] props = jniMeshGL64VertProperties(meshGL);
				for (int i = 0; i < numVert; i++)
					for (int j = 0; (j < 3) && (j < numProp); j++)
						vertices[i * 3 + j] = props[i * (int) numProp + j];
			}

			if (numTri > 0) {
				long[] tris = jniMeshGL64TriVerts(meshGL);
				System.arraycopy(tris, 0, triangles, 0, tris.length);
			}

			return new MeshData64(vertices, triangles, (int) numVert, (int) numTri);
		} finally {
			jniDeleteMeshGL64(meshGL);
		}
	}

	// ============================================================
	// Cleanup
	// ============================================================

	public void delete(long manifold) {
		jniDeleteManifold(manifold);
	}

	public void safeDelete(long m) {
		if (m == 0)
			return;
		try {
			delete(m);
		} catch (Throwable t) {
			// Already freed
		}
	}

	@Override
	public void close() {
	}

	public static boolean isNativeLibraryLoaded() {
		return loaded;
	}

	// ============================================================
	// Data records
	// ============================================================

	public record MeshData64(double[] vertices, long[] triangles, int vertCount, int triCount) {
	}

	private record RawMesh(double[] vertices, long[] triangles) {
	}

	// ============================================================
	// File I/O — STL and 3MF
	// ============================================================

	public void exportSTL(long manifold, File file) throws IOException {
		MeshData64 mesh = exportMeshGL64(manifold);
		writeBinarySTL(mesh.vertices(), mesh.triangles(), mesh.vertCount(), mesh.triCount(), file);
	}

	public void export3MF(ArrayList<Long> manifolds, File file) throws IOException {
		if (manifolds == null || manifolds.isEmpty())
			throw new IllegalArgumentException("manifolds list must not be empty");
		List<MeshData64> meshes = new ArrayList<>(manifolds.size());
		for (long seg : manifolds)
			meshes.add(exportMeshGL64(seg));
		write3MFInternal(meshes, file);
	}

	public long importSTL(File file) throws IOException {
		RawMesh raw = parseSTL(file);
		return importMeshGL64(raw.vertices(), raw.triangles(), raw.vertices().length / 3L, raw.triangles().length / 3L);
	}

	public ArrayList<Long> import3MF(File file) throws IOException {
		List<RawMesh> objects = parse3MF(file);
		ArrayList<Long> result = new ArrayList<>(objects.size());
		for (RawMesh raw : objects)
			result.add(importMeshGL64(raw.vertices(), raw.triangles(), raw.vertices().length / 3L, raw.triangles().length / 3L));
		return result;
	}

	// ============================================================
	// STL parsing/writing
	// ============================================================

	private static boolean looksLikeAsciiSTL(byte[] header80, long fileSize) {
		String prefix = new String(header80, 0, Math.min(5, header80.length), StandardCharsets.US_ASCII).trim().toLowerCase();
		if (!prefix.startsWith("solid"))
			return false;
		if (fileSize < 134)
			return true;
		return (fileSize - 84) % 50 != 0;
	}

	private static RawMesh readBinarySTL(File file) throws IOException {
		try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			dis.skipNBytes(80);
			int triCount = Integer.reverseBytes(dis.readInt());
			double[] verts = new double[triCount * 9];
			long[] tris = new long[triCount * 3];
			byte[] buf = new byte[50];
			ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
			for (int i = 0; i < triCount; i++) {
				dis.readFully(buf);
				bb.rewind();
				bb.position(12);
				int base = i * 9;
				verts[base] = bb.getFloat();
				verts[base + 1] = bb.getFloat();
				verts[base + 2] = bb.getFloat();
				verts[base + 3] = bb.getFloat();
				verts[base + 4] = bb.getFloat();
				verts[base + 5] = bb.getFloat();
				verts[base + 6] = bb.getFloat();
				verts[base + 7] = bb.getFloat();
				verts[base + 8] = bb.getFloat();
				tris[i * 3] = i * 3;
				tris[i * 3 + 1] = i * 3 + 1;
				tris[i * 3 + 2] = i * 3 + 2;
			}
			return new RawMesh(verts, tris);
		}
	}

	private static RawMesh readAsciiSTL(File file) throws IOException {
		List<Double> vList = new ArrayList<>();
		List<Long> tList = new ArrayList<>();
		long vertIdx = 0;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String line;
			long faceStart = -1;
			while ((line = br.readLine()) != null) {
				line = line.trim().toLowerCase();
				if (line.startsWith("facet normal")) {
					faceStart = vertIdx;
				} else if (line.startsWith("vertex ")) {
					String[] parts = line.split("\\s+");
					vList.add(Double.parseDouble(parts[1]));
					vList.add(Double.parseDouble(parts[2]));
					vList.add(Double.parseDouble(parts[3]));
					vertIdx++;
				} else if (line.startsWith("endfacet")) {
					tList.add(faceStart);
					tList.add(faceStart + 1);
					tList.add(faceStart + 2);
				}
			}
		}
		double[] verts = new double[vList.size()];
		for (int i = 0; i < vList.size(); i++)
			verts[i] = vList.get(i);
		long[] tris = new long[tList.size()];
		for (int i = 0; i < tList.size(); i++)
			tris[i] = tList.get(i);
		return new RawMesh(verts, tris);
	}

	private static RawMesh parseSTL(File file) throws IOException {
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] header = fis.readNBytes(80);
			return looksLikeAsciiSTL(header, file.length()) ? readAsciiSTL(file) : readBinarySTL(file);
		}
	}

	private static void writeBinarySTL(double[] verts, long[] tris, int vertCount, int triCount, File file) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(84 + triCount * 50).order(ByteOrder.LITTLE_ENDIAN);
		byte[] header = new byte[80];
		byte[] tag = "Manifold3D-Java MeshIO STL Export".getBytes(StandardCharsets.US_ASCII);
		System.arraycopy(tag, 0, header, 0, Math.min(tag.length, 80));
		buf.put(header);
		buf.putInt(triCount);
		for (int i = 0; i < triCount; i++) {
			int i0 = (int) tris[i * 3] * 3;
			int i1 = (int) tris[i * 3 + 1] * 3;
			int i2 = (int) tris[i * 3 + 2] * 3;
			float ax = (float) verts[i0], ay = (float) verts[i0 + 1], az = (float) verts[i0 + 2];
			float bx = (float) verts[i1], by = (float) verts[i1 + 1], bz = (float) verts[i1 + 2];
			float cx = (float) verts[i2], cy = (float) verts[i2 + 1], cz = (float) verts[i2 + 2];
			float ux = bx - ax, uy = by - ay, uz = bz - az;
			float vx = cx - ax, vy = cy - ay, vz = cz - az;
			float nx = uy * vz - uz * vy;
			float ny = uz * vx - ux * vz;
			float nz = ux * vy - uy * vx;
			float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
			if (len > 1e-12f) {
				nx /= len;
				ny /= len;
				nz /= len;
			}
			buf.putFloat(nx);
			buf.putFloat(ny);
			buf.putFloat(nz);
			buf.putFloat(ax);
			buf.putFloat(ay);
			buf.putFloat(az);
			buf.putFloat(bx);
			buf.putFloat(by);
			buf.putFloat(bz);
			buf.putFloat(cx);
			buf.putFloat(cy);
			buf.putFloat(cz);
			buf.putShort((short) 0);
		}
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(buf.array());
		}
	}

	// ============================================================
	// 3MF read/write
	// ============================================================

	private static List<RawMesh> parse3MF(File file) throws IOException {
		return parseModelXml(extract3DModel(file));
	}

	private static String extract3DModel(File file) throws IOException {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.getName().replace('\\', '/').equalsIgnoreCase("3D/3dmodel.model")) {
					return new String(zis.readAllBytes(), StandardCharsets.UTF_8);
				}
				zis.closeEntry();
			}
		}
		throw new IOException("No 3D/3dmodel.model entry found in 3MF archive: " + file);
	}

	private static List<RawMesh> parseModelXml(String xml) {
		List<RawMesh> result = new ArrayList<>();
		List<Double> verts = null;
		List<Long> tris = null;
		int pos = 0, len = xml.length();
		while (pos < len) {
			int tagStart = xml.indexOf('<', pos);
			if (tagStart < 0)
				break;
			int tagEnd = xml.indexOf('>', tagStart);
			if (tagEnd < 0)
				break;
			String tag = xml.substring(tagStart + 1, tagEnd).trim();
			pos = tagEnd + 1;
			if (tag.startsWith("object") && !tag.startsWith("/object")) {
				verts = new ArrayList<>();
				tris = new ArrayList<>();
			} else if (tag.equals("/object")) {
				if (verts != null && tris != null && !verts.isEmpty()) {
					double[] va = new double[verts.size()];
					for (int i = 0; i < verts.size(); i++)
						va[i] = verts.get(i);
					long[] ta = new long[tris.size()];
					for (int i = 0; i < tris.size(); i++)
						ta[i] = tris.get(i);
					result.add(new RawMesh(va, ta));
				}
				verts = null;
				tris = null;
			} else if (verts != null && tag.startsWith("vertex") && !tag.startsWith("vertices")) {
				verts.add((double) attrFloat(tag, "x"));
				verts.add((double) attrFloat(tag, "y"));
				verts.add((double) attrFloat(tag, "z"));
			} else if (tris != null && tag.startsWith("triangle") && !tag.startsWith("triangles")) {
				tris.add((long) attrInt(tag, "v1"));
				tris.add((long) attrInt(tag, "v2"));
				tris.add((long) attrInt(tag, "v3"));
			}
		}
		if (result.isEmpty())
			throw new IllegalArgumentException("3MF file contains no <object> elements with geometry");
		return result;
	}

	private static void write3MFInternal(List<MeshData64> meshes, File file) throws IOException {
		writeZip3MF(file, build3MFModelXml(meshes));
	}

	private static byte[] build3MFModelXml(List<MeshData64> meshes) {
		int cap = 512;
		for (MeshData64 m : meshes)
			cap += 200 + m.vertCount() * 60 + m.triCount() * 55;
		StringBuilder sb = new StringBuilder(cap);
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<model unit=\"millimeter\" xml:lang=\"en-US\"\n");
		sb.append("  xmlns=\"http://schemas.microsoft.com/3dmanufacturing/core/2015/02\">\n");
		sb.append("  <resources>\n");
		for (int objIdx = 0; objIdx < meshes.size(); objIdx++) {
			MeshData64 mesh = meshes.get(objIdx);
			int objectId = objIdx + 1;
			double[] v = mesh.vertices();
			long[] t = mesh.triangles();
			sb.append("    <object id=\"").append(objectId).append("\" type=\"model\">\n");
			sb.append("      <mesh>\n");
			sb.append("        <vertices>\n");
			for (int i = 0; i < mesh.vertCount(); i++) {
				sb.append("          <vertex x=\"").append(v[i * 3]).append("\" y=\"").append(v[i * 3 + 1])
						.append("\" z=\"").append(v[i * 3 + 2]).append("\"/>\n");
			}
			sb.append("        </vertices>\n");
			sb.append("        <triangles>\n");
			for (int i = 0; i < mesh.triCount(); i++) {
				sb.append("          <triangle v1=\"").append(t[i * 3]).append("\" v2=\"").append(t[i * 3 + 1])
						.append("\" v3=\"").append(t[i * 3 + 2]).append("\"/>\n");
			}
			sb.append("        </triangles>\n");
			sb.append("      </mesh>\n");
			sb.append("    </object>\n");
		}
		sb.append("  </resources>\n");
		sb.append("  <build>\n");
		for (int objIdx = 0; objIdx < meshes.size(); objIdx++)
			sb.append("    <item objectid=\"").append(objIdx + 1).append("\"/>\n");
		sb.append("  </build>\n");
		sb.append("</model>\n");
		return sb.toString().getBytes(StandardCharsets.UTF_8);
	}

	private static void writeZip3MF(File file, byte[] modelBytes) throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
			zos.setMethod(ZipOutputStream.DEFLATED);
			zos.setLevel(Deflater.BEST_SPEED);
			putZipEntry(zos, "[Content_Types].xml",
					("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
							+ "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n"
							+ "  <Default Extension=\"rels\""
							+ " ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n"
							+ "  <Default Extension=\"model\""
							+ " ContentType=\"application/vnd.ms-package.3dmanufacturing-3dmodel+xml\"/>\n"
							+ "</Types>\n").getBytes(StandardCharsets.UTF_8));
			putZipEntry(zos, "_rels/.rels",
					("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
							+ "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n"
							+ "  <Relationship Type=\"http://schemas.microsoft.com/3dmanufacturing/2013/01/3dmodel\""
							+ " Target=\"/3D/3dmodel.model\" Id=\"rel0\"/>\n"
							+ "</Relationships>\n").getBytes(StandardCharsets.UTF_8));
			putZipEntry(zos, "3D/3dmodel.model", modelBytes);
		}
	}

	private static void putZipEntry(ZipOutputStream zos, String name, byte[] data) throws IOException {
		zos.putNextEntry(new ZipEntry(name));
		zos.write(data);
		zos.closeEntry();
	}

	private static float attrFloat(String tag, String attr) {
		return Float.parseFloat(attrString(tag, attr));
	}

	private static int attrInt(String tag, String attr) {
		return Integer.parseInt(attrString(tag, attr));
	}

	private static String attrString(String tag, String attr) {
		int idx = tag.indexOf(attr + "=");
		if (idx < 0)
			throw new IllegalArgumentException("Attribute '" + attr + "' not found in tag: " + tag);
		int valStart = idx + attr.length() + 1;
		char quote = tag.charAt(valStart);
		if (quote != '"' && quote != '\'') {
			int end = valStart;
			while (end < tag.length()) {
				char c = tag.charAt(end);
				if (c == ' ' || c == '/' || c == '>')
					break;
				end++;
			}
			return tag.substring(valStart, end);
		}
		int valEnd = tag.indexOf(quote, valStart + 1);
		if (valEnd < 0)
			throw new IllegalArgumentException("Unterminated attribute for '" + attr + "' in: " + tag);
		return tag.substring(valStart + 1, valEnd);
	}
}
