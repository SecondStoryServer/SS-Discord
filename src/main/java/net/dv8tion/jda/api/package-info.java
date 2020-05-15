

/**
 * Package which contains all utilities for the JDA library.
 * These are used by JDA itself and can also be useful for the library user!
 *
 * <p>List of utilities:
 * <ul>
 *     <li>{@link net.dv8tion.jda.api.utils.MiscUtil MiscUtil}
 *     <br>Various operations that don't have specific utility classes yet, mostly internals that are accessible from JDA entities</li>
 *
 *     <li>{@link net.dv8tion.jda.api.utils.WidgetUtil WidgetUtil}
 *     <br>This is not bound to a JDA instance and can view the {@link net.dv8tion.jda.api.utils.WidgetUtil.Widget Widget}
 *         for a specified Guild. (by id)</li>
 *
 *     <li>{@link net.dv8tion.jda.api.utils.MarkdownSanitizer MarkdownSanitizer}
 *     <br>Parser for Discord markdown that can either escape or strip markdown from a string</li>
 *
 *     <li>{@link net.dv8tion.jda.api.utils.SessionController SessionController}
 *     <br>Special handler for session (re-)connects and global rate-limits</li>
 *
 *     <li>{@link net.dv8tion.jda.api.utils.TimeUtil TimeUtil}
 *     <br>Useful time conversion methods related to Discord</li>
 * </ul>
 */
package net.dv8tion.jda.api.utils;
