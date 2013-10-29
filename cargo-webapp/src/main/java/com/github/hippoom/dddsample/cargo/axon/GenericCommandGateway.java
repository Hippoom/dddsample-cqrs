package com.github.hippoom.dddsample.cargo.axon;

public interface GenericCommandGateway {

	/**
	 * Sends the given <code>command</code> and wait for it to execute. The
	 * result of the execution is returned when available. This method will
	 * block indefinitely, until a result is available, or until the Thread is
	 * interrupted. When the thread is interrupted, this method returns
	 * <code>null</code>. If command execution resulted in an exception, it is
	 * wrapped in a
	 * {@link org.axonframework.commandhandling.CommandExecutionException}.
	 * <p/>
	 * The given <code>command</code> is wrapped as the payload of the
	 * CommandMessage that is eventually posted on the Command Bus, unless
	 * Command already implements {@link org.axonframework.domain.Message}. In
	 * that case, a CommandMessage is constructed from that message's payload
	 * and MetaData.
	 * <p/>
	 * Note that the interrupted flag is set back on the thread if it has been
	 * interrupted while waiting.
	 * 
	 * @param command
	 *            The command to dispatch
	 * @param <R>
	 *            The type of result expected from command execution
	 * @return the result of command execution, or <code>null</code> if the
	 *         thread was interrupted while waiting for the command to execute
	 * 
	 * @throws org.axonframework.commandhandling.CommandExecutionException
	 *             when an exception occurred while processing the command
	 */
	<R> R sendAndWait(Object command);
}