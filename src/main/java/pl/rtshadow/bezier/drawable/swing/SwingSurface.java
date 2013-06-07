package pl.rtshadow.bezier.drawable.swing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

import pl.rtshadow.bezier.components.Coordinates;
import pl.rtshadow.bezier.drawable.Surface;

public class SwingSurface extends JPanel implements Surface {
  private final Collection<Coordinates> points = new ArrayList<>();

  public SwingSurface(int sizeX, int sizeY) {
    setSize(sizeX, sizeY);
  }

  @Override
  public void drawPoints(Collection<Coordinates> coordinates) {
    points.addAll(coordinates);

    repaint();
  }

  @Override
  public void clear() {
    points.clear();

    repaint();
  }


  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    g.setColor(Color.BLACK);
    for(Coordinates point : points) {
      g.drawRect(point.getXAsInt(), point.getYAsInt(), 1, 1);
    }
  }
}
