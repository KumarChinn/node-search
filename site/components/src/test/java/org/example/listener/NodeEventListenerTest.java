package org.example.listener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/**
 * Created by chinnku on Nov, 2021
 */
public class NodeEventListenerTest {

    EventIterator eventIterator = null;
    Event event = null;
    EventListener eventListener;


    @Before
    public void setUp() {
        eventIterator = mock(EventIterator.class);
        event = mock(Event.class);
        eventListener = new NodeEventListener();
    }

    @After
    public void tearDown() {
        eventIterator = null;
        event = null;
        eventListener = null;
    }

    @Test
    public void onEvent() throws RepositoryException {
        //Given
        when(eventIterator.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(eventIterator.nextEvent()).thenReturn(event).thenReturn(event);
        when(event.getType()).thenReturn(1);
        when(event.getIdentifier()).thenReturn("uuid-234234");
        when(event.getDate()).thenReturn(11232021L);
        when(event.getPath()).thenReturn("content/documents");
        //When
        eventListener.onEvent(eventIterator);
        //Then
        verify(eventIterator, times(3)).hasNext();
        verify(eventIterator, times(2)).nextEvent();
        verify(event, times(2)).getType();
        verify(event, times(2)).getIdentifier();
        verify(event, times(2)).getDate();
        verify(event, times(2)).getPath();
    }
}