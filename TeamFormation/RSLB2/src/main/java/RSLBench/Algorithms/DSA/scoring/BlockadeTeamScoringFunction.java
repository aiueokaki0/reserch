/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RSLBench.Algorithms.DSA.scoring;

import RSLBench.Algorithms.DSA.TargetScores;
import RSLBench.Constants;
import RSLBench.Helpers.Utility.ProblemDefinition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;

/**
 *
 * @author Marc Pujol <mpujol@iiia.csic.es>
 */
public class BlockadeTeamScoringFunction extends AbstractScoringFunction {
    private static final Logger Logger = LogManager.getLogger(BlockadeTeamScoringFunction.class);

    @Override
    public double score(EntityID agent, EntityID target, TargetScores scores, ProblemDefinition problem) {
        final int nAgents = scores.getAgentCount(target);
        CC();

        // If there is already another police agent attending that blockade, the utility of also
        // picking it is -inf
        if (nAgents > 0) {
            return Double.NEGATIVE_INFINITY;
        }

        // Otherwise we can pick it, and the value is given by the unary utility
        double utility = problem.getPoliceUtility(agent, target);
        if (problem.isPoliceAgentBlocked(agent, target)) {
            utility -= problem.getConfig().getFloatValue(Constants.KEY_BLOCKED_POLICE_PENALTY);
        }
        CC();

        // ... plus some possible penalty removals if fire agents are blocked by this blockade
        for (Pair<EntityID, EntityID> info : problem.getFireAgentsBlockedByBlockade(target)) {
            final EntityID fireAgent = info.first();
            final EntityID fire = info.second();

            if (scores.getAssignment(fireAgent).equals(fire)) {
                Logger.trace("Blockade {} is more attractive for {} because fire agent {} is blocked by it.",
                        target, agent, fireAgent);
                utility += problem.getConfig().getFloatValue(Constants.KEY_BLOCKED_FIRE_PENALTY);
            }
            CC();
        }

        return utility;
    }

}
