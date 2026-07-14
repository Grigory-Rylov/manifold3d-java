#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include "manifold/types.h"
#include "manifold/manifoldc.h"

#define PTR_FROM_LONG(l)  ((void*)(uintptr_t)(l))
#define LONG_FROM_PTR(p)  ((jlong)(uintptr_t)(p))

static JNIEnv* g_env = NULL;

JNIEXPORT void JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_nativeInit(JNIEnv* env, jclass clazz) {
    g_env = env;
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniAlloc(JNIEnv* env, jclass clazz, jlong size) {
    void* p = malloc((size_t)size);
    return LONG_FROM_PTR(p);
}

JNIEXPORT void JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniFree(JNIEnv* env, jclass clazz, jlong ptr) {
    free(PTR_FROM_LONG(ptr));
}

/* ---- Allocation ---- */

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniAllocManifold(JNIEnv* env, jclass clazz) {
    return LONG_FROM_PTR(manifold_alloc_manifold());
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniAllocMeshGL64(JNIEnv* env, jclass clazz) {
    return LONG_FROM_PTR(manifold_alloc_meshgl64());
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniAllocManifoldVec(JNIEnv* env, jclass clazz) {
    return LONG_FROM_PTR(manifold_alloc_manifold_vec_java());
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniAllocBox(JNIEnv* env, jclass clazz) {
    return LONG_FROM_PTR(manifold_alloc_box());
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniAllocPolygons(JNIEnv* env, jclass clazz) {
    return LONG_FROM_PTR(manifold_alloc_polygons());
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniAllocCrossSectionNative(JNIEnv* env, jclass clazz) {
    return LONG_FROM_PTR(manifold_alloc_cross_section());
}

/* ---- Construction ---- */

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniOfMeshGL64(JNIEnv* env, jclass clazz, jlong mem, jlong mesh) {
    return LONG_FROM_PTR(manifold_of_meshgl64(PTR_FROM_LONG(mem), PTR_FROM_LONG(mesh)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniCopy(JNIEnv* env, jclass clazz, jlong mem, jlong m) {
    return LONG_FROM_PTR(manifold_copy(PTR_FROM_LONG(mem), PTR_FROM_LONG(m)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniEmpty(JNIEnv* env, jclass clazz, jlong mem) {
    return LONG_FROM_PTR(manifold_empty(PTR_FROM_LONG(mem)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniTetrahedron(JNIEnv* env, jclass clazz, jlong mem) {
    return LONG_FROM_PTR(manifold_tetrahedron(PTR_FROM_LONG(mem)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniCube(JNIEnv* env, jclass clazz, jlong mem, jdouble x, jdouble y, jdouble z, jint center) {
    return LONG_FROM_PTR(manifold_cube(PTR_FROM_LONG(mem), x, y, z, center));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniSphere(JNIEnv* env, jclass clazz, jlong mem, jdouble radius, jint segs) {
    return LONG_FROM_PTR(manifold_sphere(PTR_FROM_LONG(mem), radius, segs));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniCylinder(JNIEnv* env, jclass clazz, jlong mem, jdouble height, jdouble rlow, jdouble rhigh, jint segs, jint center) {
    return LONG_FROM_PTR(manifold_cylinder(PTR_FROM_LONG(mem), height, rlow, rhigh, segs, center));
}

/* ---- Boolean ---- */

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniBoolean(JNIEnv* env, jclass clazz, jlong mem, jlong a, jlong b, jint op) {
    return LONG_FROM_PTR(manifold_boolean(PTR_FROM_LONG(mem), PTR_FROM_LONG(a), PTR_FROM_LONG(b), (ManifoldOpType)op));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniUnion(JNIEnv* env, jclass clazz, jlong mem, jlong a, jlong b) {
    return LONG_FROM_PTR(manifold_union(PTR_FROM_LONG(mem), PTR_FROM_LONG(a), PTR_FROM_LONG(b)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniDifference(JNIEnv* env, jclass clazz, jlong mem, jlong a, jlong b) {
    return LONG_FROM_PTR(manifold_difference(PTR_FROM_LONG(mem), PTR_FROM_LONG(a), PTR_FROM_LONG(b)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniIntersection(JNIEnv* env, jclass clazz, jlong mem, jlong a, jlong b) {
    return LONG_FROM_PTR(manifold_intersection(PTR_FROM_LONG(mem), PTR_FROM_LONG(a), PTR_FROM_LONG(b)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniMinkowskiSum(JNIEnv* env, jclass clazz, jlong mem, jlong a, jlong b) {
    return LONG_FROM_PTR(manifold_minkowski_sum(PTR_FROM_LONG(mem), PTR_FROM_LONG(a), PTR_FROM_LONG(b)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniMinkowskiDifference(JNIEnv* env, jclass clazz, jlong mem, jlong a, jlong b) {
    return LONG_FROM_PTR(manifold_minkowski_difference(PTR_FROM_LONG(mem), PTR_FROM_LONG(a), PTR_FROM_LONG(b)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniBatchBoolean(JNIEnv* env, jclass clazz, jlong mem, jlong ms, jint op) {
    return LONG_FROM_PTR(manifold_batch_boolean(PTR_FROM_LONG(mem), PTR_FROM_LONG(ms), (ManifoldOpType)op));
}

JNIEXPORT void JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniManifoldVecPushBack(JNIEnv* env, jclass clazz, jlong ms, jlong m) {
    manifold_manifold_vec_push_back(PTR_FROM_LONG(ms), PTR_FROM_LONG(m));
}

/* ---- Transforms ---- */

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniTransform(JNIEnv* env, jclass clazz, jlong mem, jlong m,
        jdouble x1, jdouble y1, jdouble z1, jdouble x2, jdouble y2, jdouble z2,
        jdouble x3, jdouble y3, jdouble z3, jdouble x4, jdouble y4, jdouble z4) {
    return LONG_FROM_PTR(manifold_transform(PTR_FROM_LONG(mem), PTR_FROM_LONG(m),
        x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniTranslate(JNIEnv* env, jclass clazz, jlong mem, jlong m, jdouble x, jdouble y, jdouble z) {
    return LONG_FROM_PTR(manifold_translate(PTR_FROM_LONG(mem), PTR_FROM_LONG(m), x, y, z));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniScale(JNIEnv* env, jclass clazz, jlong mem, jlong m, jdouble x, jdouble y, jdouble z) {
    return LONG_FROM_PTR(manifold_scale(PTR_FROM_LONG(mem), PTR_FROM_LONG(m), x, y, z));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniMirror(JNIEnv* env, jclass clazz, jlong mem, jlong m, jdouble nx, jdouble ny, jdouble nz) {
    return LONG_FROM_PTR(manifold_mirror(PTR_FROM_LONG(mem), PTR_FROM_LONG(m), nx, ny, nz));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniRotate(JNIEnv* env, jclass clazz, jlong mem, jlong m, jdouble x, jdouble y, jdouble z) {
    return LONG_FROM_PTR(manifold_rotate(PTR_FROM_LONG(mem), PTR_FROM_LONG(m), x, y, z));
}

/* ---- Refinement ---- */

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniRefine(JNIEnv* env, jclass clazz, jlong mem, jlong m, jint level) {
    return LONG_FROM_PTR(manifold_refine(PTR_FROM_LONG(mem), PTR_FROM_LONG(m), level));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniRefineToLength(JNIEnv* env, jclass clazz, jlong mem, jlong m, jdouble length) {
    return LONG_FROM_PTR(manifold_refine_to_length(PTR_FROM_LONG(mem), PTR_FROM_LONG(m), length));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniRefineToTolerance(JNIEnv* env, jclass clazz, jlong mem, jlong m, jdouble tolerance) {
    return LONG_FROM_PTR(manifold_refine_to_tolerance(PTR_FROM_LONG(mem), PTR_FROM_LONG(m), tolerance));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniSimplify(JNIEnv* env, jclass clazz, jlong mem, jlong m, jdouble tolerance) {
    return LONG_FROM_PTR(manifold_simplify(PTR_FROM_LONG(mem), PTR_FROM_LONG(m), tolerance));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniSmoothByNormals(JNIEnv* env, jclass clazz, jlong mem, jlong m, jint normalIdx) {
    return LONG_FROM_PTR(manifold_smooth_by_normals(PTR_FROM_LONG(mem), PTR_FROM_LONG(m), normalIdx));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniCalculateNormals(JNIEnv* env, jclass clazz, jlong mem, jlong m, jint normalIdx, jdouble minSharpAngle) {
    return LONG_FROM_PTR(manifold_calculate_normals(PTR_FROM_LONG(mem), PTR_FROM_LONG(m), normalIdx, minSharpAngle));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniSmoothOut(JNIEnv* env, jclass clazz, jlong mem, jlong m, jdouble minSharpAngle, jdouble minSmoothness) {
    return LONG_FROM_PTR(manifold_smooth_out(PTR_FROM_LONG(mem), PTR_FROM_LONG(m), minSharpAngle, minSmoothness));
}

/* ---- Split/Trim ---- */

JNIEXPORT jlongArray JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniSplit(JNIEnv* env, jclass clazz, jlong a, jlong b) {
    jlong firstMem = (jlong)LONG_FROM_PTR(manifold_alloc_manifold());
    jlong secondMem = (jlong)LONG_FROM_PTR(manifold_alloc_manifold());
    ManifoldManifoldPair pair = manifold_split(PTR_FROM_LONG(firstMem), PTR_FROM_LONG(secondMem), PTR_FROM_LONG(a), PTR_FROM_LONG(b));
    jlongArray result = (*env)->NewLongArray(env, 2);
    jlong vals[2] = { LONG_FROM_PTR(pair.first), LONG_FROM_PTR(pair.second) };
    (*env)->SetLongArrayRegion(env, result, 0, 2, vals);
    return result;
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniTrimByPlane(JNIEnv* env, jclass clazz, jlong mem, jlong m, jdouble nx, jdouble ny, jdouble nz, jdouble offset) {
    return LONG_FROM_PTR(manifold_trim_by_plane(PTR_FROM_LONG(mem), PTR_FROM_LONG(m), nx, ny, nz, offset));
}

JNIEXPORT jlongArray JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniSplitByPlane(JNIEnv* env, jclass clazz, jlong m, jdouble nx, jdouble ny, jdouble nz, jdouble offset) {
    jlong firstMem = (jlong)LONG_FROM_PTR(manifold_alloc_manifold());
    jlong secondMem = (jlong)LONG_FROM_PTR(manifold_alloc_manifold());
    ManifoldManifoldPair pair = manifold_split_by_plane(PTR_FROM_LONG(firstMem), PTR_FROM_LONG(secondMem),
        PTR_FROM_LONG(m), nx, ny, nz, offset);
    jlongArray result = (*env)->NewLongArray(env, 2);
    jlong vals[2] = { LONG_FROM_PTR(pair.first), LONG_FROM_PTR(pair.second) };
    (*env)->SetLongArrayRegion(env, result, 0, 2, vals);
    return result;
}

/* ---- Slice ---- */

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniSlice(JNIEnv* env, jclass clazz, jlong mem, jlong m, jdouble height) {
    return LONG_FROM_PTR(manifold_slice(PTR_FROM_LONG(mem), PTR_FROM_LONG(m), height));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniPolygonsLength(JNIEnv* env, jclass clazz, jlong ps) {
    return (jlong)manifold_polygons_length(PTR_FROM_LONG(ps));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniPolygonsSimpleLength(JNIEnv* env, jclass clazz, jlong ps, jlong idx) {
    return (jlong)manifold_polygons_simple_length(PTR_FROM_LONG(ps), (size_t)idx);
}

JNIEXPORT jdoubleArray JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniPolygonsGetPoint(JNIEnv* env, jclass clazz, jlong ps, jlong simpleIdx, jlong ptIdx) {
    ManifoldVec2 pt = manifold_polygons_get_point(PTR_FROM_LONG(ps), (size_t)simpleIdx, (size_t)ptIdx);
    jdoubleArray result = (*env)->NewDoubleArray(env, 2);
    jdouble vals[2] = { pt.x, pt.y };
    (*env)->SetDoubleArrayRegion(env, result, 0, 2, vals);
    return result;
}

/* ---- Analysis ---- */

JNIEXPORT jdouble JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniVolume(JNIEnv* env, jclass clazz, jlong m) {
    return manifold_volume(PTR_FROM_LONG(m));
}

JNIEXPORT jdouble JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniSurfaceArea(JNIEnv* env, jclass clazz, jlong m) {
    return manifold_surface_area(PTR_FROM_LONG(m));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniBoundingBox(JNIEnv* env, jclass clazz, jlong mem, jlong m) {
    return LONG_FROM_PTR(manifold_bounding_box(PTR_FROM_LONG(mem), PTR_FROM_LONG(m)));
}

JNIEXPORT jdoubleArray JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniBoxMin(JNIEnv* env, jclass clazz, jlong box) {
    ManifoldVec3 v = manifold_box_min(PTR_FROM_LONG(box));
    jdoubleArray result = (*env)->NewDoubleArray(env, 3);
    jdouble vals[3] = { v.x, v.y, v.z };
    (*env)->SetDoubleArrayRegion(env, result, 0, 3, vals);
    return result;
}

JNIEXPORT jdoubleArray JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniBoxMax(JNIEnv* env, jclass clazz, jlong box) {
    ManifoldVec3 v = manifold_box_max(PTR_FROM_LONG(box));
    jdoubleArray result = (*env)->NewDoubleArray(env, 3);
    jdouble vals[3] = { v.x, v.y, v.z };
    (*env)->SetDoubleArrayRegion(env, result, 0, 3, vals);
    return result;
}

JNIEXPORT jdoubleArray JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniBoxCenter(JNIEnv* env, jclass clazz, jlong box) {
    ManifoldVec3 v = manifold_box_center(PTR_FROM_LONG(box));
    jdoubleArray result = (*env)->NewDoubleArray(env, 3);
    jdouble vals[3] = { v.x, v.y, v.z };
    (*env)->SetDoubleArrayRegion(env, result, 0, 3, vals);
    return result;
}

JNIEXPORT jdoubleArray JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniBoxDimensions(JNIEnv* env, jclass clazz, jlong box) {
    ManifoldVec3 v = manifold_box_dimensions(PTR_FROM_LONG(box));
    jdoubleArray result = (*env)->NewDoubleArray(env, 3);
    jdouble vals[3] = { v.x, v.y, v.z };
    (*env)->SetDoubleArrayRegion(env, result, 0, 3, vals);
    return result;
}

JNIEXPORT jdouble JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniEpsilon(JNIEnv* env, jclass clazz, jlong m) {
    return manifold_epsilon(PTR_FROM_LONG(m));
}

JNIEXPORT jint JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniGenus(JNIEnv* env, jclass clazz, jlong m) {
    return manifold_genus(PTR_FROM_LONG(m));
}

/* ---- Composition ---- */

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniCompose(JNIEnv* env, jclass clazz, jlong mem, jlong ms) {
    return LONG_FROM_PTR(manifold_compose(PTR_FROM_LONG(mem), PTR_FROM_LONG(ms)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniDecompose(JNIEnv* env, jclass clazz, jlong mem, jlong m) {
    return LONG_FROM_PTR(manifold_decompose(PTR_FROM_LONG(mem), PTR_FROM_LONG(m)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniAsOriginal(JNIEnv* env, jclass clazz, jlong mem, jlong m) {
    return LONG_FROM_PTR(manifold_as_original(PTR_FROM_LONG(mem), PTR_FROM_LONG(m)));
}

/* ---- Hull ---- */

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniHull(JNIEnv* env, jclass clazz, jlong mem, jlong m) {
    return LONG_FROM_PTR(manifold_hull(PTR_FROM_LONG(mem), PTR_FROM_LONG(m)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniHullPts(JNIEnv* env, jclass clazz, jlong mem, jdoubleArray pts, jlong count) {
    jdouble* ptData = (*env)->GetDoubleArrayElements(env, pts, NULL);
    ManifoldVec3* vecs = (ManifoldVec3*)malloc((size_t)count * sizeof(ManifoldVec3));
    for (jsize i = 0; i < count; i++) {
        vecs[i].x = ptData[i * 3];
        vecs[i].y = ptData[i * 3 + 1];
        vecs[i].z = ptData[i * 3 + 2];
    }
    (*env)->ReleaseDoubleArrayElements(env, pts, ptData, JNI_ABORT);
    jlong result = LONG_FROM_PTR(manifold_hull_pts(PTR_FROM_LONG(mem), vecs, (size_t)count));
    free(vecs);
    return result;
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniBatchHull(JNIEnv* env, jclass clazz, jlong mem, jlong ms) {
    return LONG_FROM_PTR(manifold_batch_hull(PTR_FROM_LONG(mem), PTR_FROM_LONG(ms)));
}

/* ---- Info ---- */

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniManifoldSize(JNIEnv* env, jclass clazz) {
    return (jlong)manifold_manifold_size();
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniMeshGL64Size(JNIEnv* env, jclass clazz) {
    return (jlong)manifold_meshgl64_size();
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniNumEdge(JNIEnv* env, jclass clazz, jlong m) {
    return (jlong)manifold_num_edge(PTR_FROM_LONG(m));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniNumProp(JNIEnv* env, jclass clazz, jlong m) {
    return (jlong)manifold_num_prop(PTR_FROM_LONG(m));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniNumVert(JNIEnv* env, jclass clazz, jlong m) {
    return (jlong)manifold_num_vert(PTR_FROM_LONG(m));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniNumTri(JNIEnv* env, jclass clazz, jlong m) {
    return (jlong)manifold_num_tri(PTR_FROM_LONG(m));
}

JNIEXPORT jint JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniStatus(JNIEnv* env, jclass clazz, jlong m) {
    return (jint)manifold_status(PTR_FROM_LONG(m));
}

JNIEXPORT jint JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniIsEmpty(JNIEnv* env, jclass clazz, jlong m) {
    return manifold_is_empty(PTR_FROM_LONG(m));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniManifoldVecSize(JNIEnv* env, jclass clazz) {
    return (jlong)manifold_manifold_vec_size();
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniManifoldVecLength(JNIEnv* env, jclass clazz, jlong ms) {
    return (jlong)manifold_manifold_vec_length(PTR_FROM_LONG(ms));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniManifoldVecGet(JNIEnv* env, jclass clazz, jlong mem, jlong ms, jlong idx) {
    return LONG_FROM_PTR(manifold_manifold_vec_get(PTR_FROM_LONG(mem), PTR_FROM_LONG(ms), (size_t)idx));
}

/* ---- MeshGL64 ---- */

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniGetMeshGL64(JNIEnv* env, jclass clazz, jlong mem, jlong m) {
    return LONG_FROM_PTR(manifold_get_meshgl64(PTR_FROM_LONG(mem), PTR_FROM_LONG(m)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniMeshGL64NumVert(JNIEnv* env, jclass clazz, jlong m) {
    return (jlong)manifold_meshgl64_num_vert(PTR_FROM_LONG(m));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniMeshGL64NumTri(JNIEnv* env, jclass clazz, jlong m) {
    return (jlong)manifold_meshgl64_num_tri(PTR_FROM_LONG(m));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniMeshGL64NumProp(JNIEnv* env, jclass clazz, jlong m) {
    return (jlong)manifold_meshgl64_num_prop(PTR_FROM_LONG(m));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniMeshGL64VertPropertiesLength(JNIEnv* env, jclass clazz, jlong m) {
    return (jlong)manifold_meshgl64_vert_properties_length(PTR_FROM_LONG(m));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniMeshGL64TriLength(JNIEnv* env, jclass clazz, jlong m) {
    return (jlong)manifold_meshgl64_tri_length(PTR_FROM_LONG(m));
}

JNIEXPORT jdoubleArray JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniMeshGL64VertProperties(JNIEnv* env, jclass clazz, jlong m) {
    size_t len = manifold_meshgl64_vert_properties_length(PTR_FROM_LONG(m));
    void* mem = malloc(len * sizeof(double));
    double* props = manifold_meshgl64_vert_properties(mem, PTR_FROM_LONG(m));
    jdoubleArray result = (*env)->NewDoubleArray(env, (jsize)len);
    (*env)->SetDoubleArrayRegion(env, result, 0, (jsize)len, props);
    free(mem);
    return result;
}

JNIEXPORT jlongArray JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniMeshGL64TriVerts(JNIEnv* env, jclass clazz, jlong m) {
    size_t len = manifold_meshgl64_tri_length(PTR_FROM_LONG(m));
    void* mem = malloc(len * sizeof(uint64_t));
    uint64_t* tris = manifold_meshgl64_tri_verts(mem, PTR_FROM_LONG(m));
    jlongArray result = (*env)->NewLongArray(env, (jsize)len);
    jlong* jlongs = (jlong*)tris;
    (*env)->SetLongArrayRegion(env, result, 0, (jsize)len, jlongs);
    free(mem);
    return result;
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniMeshGL64Merge(JNIEnv* env, jclass clazz, jlong mem, jlong m) {
    return LONG_FROM_PTR(manifold_meshgl64_merge(PTR_FROM_LONG(mem), PTR_FROM_LONG(m)));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniMeshGL64(JNIEnv* env, jclass clazz, jlong mem,
        jdoubleArray vertProps, jlong nVerts, jlong nProps, jlongArray triVerts, jlong nTris) {
    double* vp = (double*)(*env)->GetDoubleArrayElements(env, vertProps, NULL);
    jlong* tv = (*env)->GetLongArrayElements(env, triVerts, NULL);
    jlong result = LONG_FROM_PTR(manifold_meshgl64(
        PTR_FROM_LONG(mem), vp, (size_t)nVerts, (size_t)nProps, (uint64_t*)tv, (size_t)nTris));
    (*env)->ReleaseDoubleArrayElements(env, vertProps, vp, JNI_ABORT);
    (*env)->ReleaseLongArrayElements(env, triVerts, tv, JNI_ABORT);
    return result;
}

/* ---- Cleanup ---- */

JNIEXPORT void JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniDeleteManifold(JNIEnv* env, jclass clazz, jlong m) {
    manifold_delete_manifold(PTR_FROM_LONG(m));
}

JNIEXPORT void JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniDeleteMeshGL64(JNIEnv* env, jclass clazz, jlong m) {
    manifold_delete_meshgl64(PTR_FROM_LONG(m));
}

JNIEXPORT void JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniDeleteManifoldVec(JNIEnv* env, jclass clazz, jlong ms) {
    manifold_delete_manifold_vec(PTR_FROM_LONG(ms));
}

JNIEXPORT void JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniDeleteBox(JNIEnv* env, jclass clazz, jlong b) {
    manifold_delete_box(PTR_FROM_LONG(b));
}

JNIEXPORT void JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniDeletePolygons(JNIEnv* env, jclass clazz, jlong p) {
    manifold_delete_polygons(PTR_FROM_LONG(p));
}

JNIEXPORT void JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniDeleteCrossSection(JNIEnv* env, jclass clazz, jlong cs) {
    manifold_delete_cross_section(PTR_FROM_LONG(cs));
}

/* ---- Quality globals ---- */

JNIEXPORT void JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniSetCircularSegments(JNIEnv* env, jclass clazz, jint number) {
    manifold_set_circular_segments(number);
}

JNIEXPORT void JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniSetMinCircularAngle(JNIEnv* env, jclass clazz, jdouble degrees) {
    manifold_set_min_circular_angle(degrees);
}

JNIEXPORT jint JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniGetCircularSegments(JNIEnv* env, jclass clazz, jdouble radius) {
    return manifold_get_circular_segments(radius);
}

/* ---- CrossSection ---- */

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniCrossSectionSize(JNIEnv* env, jclass clazz) {
    return (jlong)manifold_cross_section_size();
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniCrossSectionOfPolygons(JNIEnv* env, jclass clazz, jlong mem, jlong ps, jint fr) {
    return LONG_FROM_PTR(manifold_cross_section_of_polygons(PTR_FROM_LONG(mem), PTR_FROM_LONG(ps), (ManifoldFillRule)fr));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniCrossSectionOffset(JNIEnv* env, jclass clazz, jlong mem, jlong cs,
        jdouble delta, jint jt, jdouble miterLimit, jint circularSegments) {
    return LONG_FROM_PTR(manifold_cross_section_offset(PTR_FROM_LONG(mem), PTR_FROM_LONG(cs), delta,
        (ManifoldJoinType)jt, miterLimit, circularSegments));
}

JNIEXPORT jlong JNICALL
Java_com_cadoodlecad_manifold_ManifoldBindings_jniCrossSectionToPolygons(JNIEnv* env, jclass clazz, jlong mem, jlong cs) {
    return LONG_FROM_PTR(manifold_cross_section_to_polygons(PTR_FROM_LONG(mem), PTR_FROM_LONG(cs)));
}
