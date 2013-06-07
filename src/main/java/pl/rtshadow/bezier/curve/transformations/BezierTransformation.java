package pl.rtshadow.bezier.curve.transformations;

import java.util.List;

import pl.rtshadow.bezier.components.Coordinates;

public interface BezierTransformation {
  List<Coordinates> apply(List<Coordinates> controlPoints);
}
