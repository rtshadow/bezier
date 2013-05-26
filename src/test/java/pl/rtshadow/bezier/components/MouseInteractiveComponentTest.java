/*
 *  Copyright Pastuszka Przemyslaw, University of Wroclaw, Poland (c) 2013.
 */

package pl.rtshadow.bezier.components;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.rtshadow.bezier.bridge.components.ExternalMouseDrivenComponent;
import pl.rtshadow.bezier.bridge.events.MouseAction;
import pl.rtshadow.bezier.bridge.events.MouseActionListener;
import pl.rtshadow.bezier.components.listeners.RemovalListener;

@RunWith(MockitoJUnitRunner.class)
public class MouseInteractiveComponentTest {
  @Mock
  private RemovalListener removalListener;
  @Mock
  private ExternalMouseDrivenComponent externalMouseDrivenComponent;

  private List<MouseActionListener> registeredListeners;
  private InteractiveComponent mouseInteractiveComponent;

  @Before
  public void setup() {
    mouseInteractiveComponent = new MouseInteractiveComponent(externalMouseDrivenComponent);

    registeredListeners = retrieveRegisteredListeners();
  }

  @Test
  public void notifiesListenersOnRightClickAndRemovesUnderlyingComponent() {
    mouseInteractiveComponent.addRemovalListener(removalListener);

    notifyAll(new MouseAction(new Coordinates(0, 0), MouseAction.ButtonPressed.RIGHT));

    verify(removalListener).onRemoval();
    verify(externalMouseDrivenComponent).remove();
  }

  @Test
  public void doesNothingForLeftClick() {
    mouseInteractiveComponent.addRemovalListener(removalListener);

    notifyAll(new MouseAction(new Coordinates(0, 0), MouseAction.ButtonPressed.LEFT));

    verify(removalListener, never()).onRemoval();
    verify(externalMouseDrivenComponent, never()).remove();
  }

  private List<MouseActionListener> retrieveRegisteredListeners() {
    ArgumentCaptor<MouseActionListener> captor = ArgumentCaptor.forClass(MouseActionListener.class);

    verify(externalMouseDrivenComponent, atLeastOnce()).addMousePressedListener(captor.capture());
    return captor.getAllValues();
  }

  private void notifyAll(MouseAction mouseAction) {
    for (MouseActionListener listener : registeredListeners) {
      listener.onMouseAction(mouseAction);
    }
  }
}
