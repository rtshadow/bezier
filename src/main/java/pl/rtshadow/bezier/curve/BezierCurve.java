/*
 *  Copyright Pastuszka Przemyslaw, University of Wroclaw, Poland (c) 2013.
 */

package pl.rtshadow.bezier.curve;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.abs;

import java.awt.*;
import java.util.List;

import pl.rtshadow.bezier.components.Coordinates;
import pl.rtshadow.bezier.components.InteractiveComponentsList;
import pl.rtshadow.bezier.curve.evaluation.BezierEvaluationAlgorithm;
import pl.rtshadow.bezier.curve.transformations.BezierTransformation;
import pl.rtshadow.bezier.drawable.Surface;
import pl.rtshadow.bezier.util.BoundedIterable;

public class BezierCurve {
  private final InteractiveComponentsList controlPoints;
  private final BezierEvaluationAlgorithm evaluationAlgorithm;
  private final Color color;

  public BezierCurve(InteractiveComponentsList controlPoints, BezierEvaluationAlgorithm evaluationAlgorithm, Color color) {
    this.controlPoints = controlPoints;
    this.evaluationAlgorithm = evaluationAlgorithm;
    this.color = color;
  }

  public void draw(Surface surface) {
    if (controlPoints.getSize() >= 2) {
      surface.drawPoints(computeDrawPoints(controlPoints), color);
    }
  }

  public List<Coordinates> transformation(BezierTransformation transformation) {
    return transformation.apply(controlPoints);
  }

  public void deactivate() {
    controlPoints.deactivate();
  }

  private List<Coordinates> computeDrawPoints(BoundedIterable<Coordinates> controlPoints) {
    List<Coordinates> pointsToDraw = newArrayList();

    double step = 0.01f;

    Coordinates previousPoint = evaluationAlgorithm.evaluatePoint(controlPoints, 0);
    pointsToDraw.add(previousPoint);

    for (double t = 0; t <= 1 - step; t += step) {

      while (true) {
        Coordinates nextPointCandidate = evaluationAlgorithm.evaluatePoint(controlPoints, t + step);
        if (areClose(previousPoint, nextPointCandidate)) {
          pointsToDraw.add(nextPointCandidate);
          previousPoint = nextPointCandidate;
          break;
        }

        step /= 2;
      }

    }
    return pointsToDraw;
  }

  private boolean areClose(Coordinates a, Coordinates b) {
    return abs(a.getX() - b.getX()) <= 1 && abs(a.getY() - b.getY()) <= 1;
  }
}
