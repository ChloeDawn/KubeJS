package dev.latvian.mods.kubejs.script;


import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

/**
 * @author LatvianModder
 */
public class ScriptsLoadedEvent {
	public static final Event<Runnable> EVENT = EventFactory.createLoop(Runnable.class);
}