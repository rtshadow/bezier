/*
 *  Copyright Pastuszka Przemyslaw, University of Wroclaw, Poland (c) 2013.
 */

package pl.rtshadow.bezier.curve.transformations;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static java.util.Collections.nCopies;
import static pl.rtshadow.bezier.util.Coordinates.add;
import static pl.rtshadow.bezier.util.Coordinates.multiply;

import java.util.List;

import pl.rtshadow.bezier.util.BoundedIterable;
import pl.rtshadow.bezier.util.Coordinate;

/**
 * Reduces Bezier curve degree by performing inversion of degree elevation process
 *
 * Degree elevation process can be described by the equation:
 * b_i = 1/n (i * b'_{i-1} + (n - 1) * b'_i) for i = 0 to n
 * where b_i are coeffs of higher degree curve (result of the process)
 * and b'_i are coeffs of lower degree curve (one being elevated)
 *
 * By simple transformation on above equation one can get other equations, which allow
 * to compute b'_i coeffs from given b_i
 */
public abstract class DegreeElevationInversion implements BezierTransformation {

  /**
   * This is method proposed by Forrest (1972)
   *
   * First compute two sets of control points using two different forms of elevation iversion
   * Then blend those two sets using given alpha parameters
   * Using different alpha parameters one can get different algorithms
   */
  @Override
  public List<Coordinate> apply(BoundedIterable<Coordinate> controlPoints) {
    checkArgument(controlPoints.getSize() > 2);

    List<Coordinate> controlPointsCopy = newArrayList(controlPoints);

    int n = controlPoints.getSize() - 1;

    List<Coordinate> bOneControlPoints = computeFirstPoints(controlPointsCopy, n);
    List<Coordinate> bTwoControlPoints = computeSecondPoints(controlPointsCopy, n);
    return blend(n, bOneControlPoints, bTwoControlPoints);
  }

  /**
   * Computes control points using equation:
   * 1/(n - i) * (n * b_i - i * b'_{i-1}) for i = 0 to n - 1
   * where b_i are coeffs of higher degree curve (one being reduced)
   * and b'_i are coeffs of lower degree curve (result of the operation)
   */
  private List<Coordinate> computeFirstPoints(List<Coordinate> controlPoints, int n) {
    List<Coordinate> bOneControlPoints = newArrayListWithCapacity(n);
    bOneControlPoints.add(controlPoints.get(0));
    for (int i = 1; i <= n - 1; ++i) {
      Coordinate lastPoint = bOneControlPoints.get(i - 1);
      Coordinate nextPoint = multiply(1f / ((float) n - i),
          add(
              multiply(n, controlPoints.get(i)),
              multiply(-i, lastPoint)));

      bOneControlPoints.add(nextPoint);
    }
    return bOneControlPoints;
  }

  /**
   * Computes control points using equation:
   * 1/i * (n * b_i - (n - i) * b'_i) for i = n to 1
   * where b_i are coeffs of higher degree curve (one being reduced)
   * and b'_i are coeffs of lower degree curve (result of the operation)
   */
  private List<Coordinate> computeSecondPoints(List<Coordinate> controlPoints, int n) {
    List<Coordinate> bTwoControlPoints = newArrayList(nCopies(n, new Coordinate(0, 0)));
    bTwoControlPoints.set(n - 1, controlPoints.get(n));
    for (int i = n - 1; i >= 1; --i) {
      Coordinate lastPoint = bTwoControlPoints.get(i);
      Coordinate nextPoint = multiply(1f / (float) i,
          add(
              multiply(n, controlPoints.get(i)),
              multiply(i - n, lastPoint)));

      bTwoControlPoints.set(i - 1, nextPoint);
    }
    return bTwoControlPoints;
  }

  private List<Coordinate> blend(int n, List<Coordinate> bOneControlPoints, List<Coordinate> bTwoControlPoints) {
    List<Coordinate> newControlPoints = newArrayListWithCapacity(n);
    List<Double> alphas = getAlphas(n);
    for (int i = 0; i <= n - 1; ++i) {
      Coordinate nextPoint =
          add(
              multiply(1 - alphas.get(i), bOneControlPoints.get(i)),
              multiply(alphas.get(i), bTwoControlPoints.get(i)));
      newControlPoints.add(nextPoint);
    }
    return newControlPoints;
  }

  protected abstract List<Double> getAlphas(int n);
}
