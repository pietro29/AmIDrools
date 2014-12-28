package ami_drools;

import java.util.HashSet;
import java.util.LinkedList;


public class WoisRegistration {
	
	/** The registered WoIS */
    final Wois wois;
    /** Name of the module used for shared facts for the WoIS {@link #wois}. */
    final String defmodule;
    /** Name of the IS in the WoIS {@link #wois}. */
    final String name;
    /**
     * State of subscription. If equal to {@link #IN}, the IS is registered. If equal to
     * {@link #ENTERING}, the IS registering; effects of operations done in the WoIS in this state
     * should be the same as if the state were <code>IN</code>. If equal to {@link #OUT}, the IS
     * is not registered. If equal to {@link #EXITING}, the IS cancelling its registration; effects
     * of operations done in the WoIS in this state should be the same as if the state were
     * <code>OUT</code>. Access to this field should be synchronized on {@link Is#woises}.
     */
    int state;
    /** @see #state */
    static final int INVALID = 0;
    /** @see #state */
    static final int ENTERING = 1;
    /** @see #state */
    static final int IN = 2;
    /** @see #state */
    static final int EXITING = 3;
    /** @see #state */
    static final int OUT = 4;
    public WoisRegistration( Wois wois, String defmodule, String name )
    {
        this.wois = wois;
        this.defmodule = defmodule;
        this.name = name;
        //myGhosts = new HashSet();
        //modificationQueue = new LinkedList();
    }

    boolean isRegistered()
    {
        return state == IN || state == ENTERING;
    }
}
