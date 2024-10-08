package com.mycompany.app;

/**
 * Input Line Types
 */
public enum LineType {
    /**
     * in this line is an action
     */
    act,

    /**
     * in this line is a precondition
     */
    pre,

    /**
     * in this line is an effect
     */
    eff,

    /**
     * in this line is a start state
     */
    s0,

    /**
     * in this line is a final state
     */
    sz,

    /**
     * this line is empty ? todo
     */
    emt,

    /**
     * todo (should probably be removed -> throw exceptions instead)
     */
    noType
}
