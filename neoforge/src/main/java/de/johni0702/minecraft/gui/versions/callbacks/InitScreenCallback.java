package de.johni0702.minecraft.gui.versions.callbacks;

import de.johni0702.minecraft.gui.utils.Event;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.Collection;

public interface InitScreenCallback {
    Event<InitScreenCallback> EVENT = Event.create((listeners) ->
            (screen, buttons) -> {
                for (InitScreenCallback listener : listeners) {
                    listener.initScreen(screen, buttons);
                }
            }
    );

    void initScreen(Screen screen, Collection<AbstractWidget> buttons);

    interface Pre {
        Event<InitScreenCallback.Pre> EVENT = Event.create((listeners) ->
                (screen) -> {
                    for (InitScreenCallback.Pre listener : listeners) {
                        listener.preInitScreen(screen);
                    }
                }
        );

        void preInitScreen(Screen screen);
    }
}
