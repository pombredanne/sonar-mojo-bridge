/*
 * sonar-mojo-bridge-maven-enforcer
 * Copyright (C) 2012 Lars Gohlke
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package de.lgohlke.sonar.maven.enforcer;

import de.lgohlke.sonar.maven.BridgeMojo;
import de.lgohlke.sonar.maven.Goal;
import lombok.Setter;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.enforcer.DefaultEnforcementRuleHelper;
import org.apache.maven.plugins.enforcer.EnforceMojo;
import org.apache.maven.plugins.enforcer.EnforcerExpressionEvaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * User: lars
 */
@Goal("enforce")
public class EnforceBridgeMojo extends EnforceMojo implements BridgeMojo<RuleTransferHandler> {
  @Setter
  private RuleTransferHandler resultHandler;

  @Override
  public void execute() throws MojoExecutionException {
    Log log = this.getLog();

    EnforcerExpressionEvaluator evaluator = new EnforcerExpressionEvaluator(session, translator, project);

    EnforcerRule[] rules = resultHandler.getRules().toArray(new EnforcerRule[resultHandler.getRules().size()]);

    // the entire execution can be easily skipped
    if (!skip) {
      // list to store exceptions
      List<String> list = new ArrayList<String>();

      // make sure the rules exist
      if (rules.length > 0) {

        // create my helper
        EnforcerRuleHelper helper = new DefaultEnforcementRuleHelper(session, evaluator, log, container);

        //        helper.getComponent()
        // if we are only warning, then disable
        // failFast
        if (!fail) {
          failFast = false;
        }

        // go through each rule
        for (int i = 0; i < rules.length; i++) {
          // prevent against empty rules
          EnforcerRule rule = rules[i];
          if (rule != null) {
            // store the current rule for
            // logging purposes
            String currentRule = rule.getClass().getName();
            log.debug("Executing rule: " + currentRule);
            try {
              if (ignoreCache || shouldExecute(rule)) {
                // execute the rule
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized (rule) {
                  rule.execute(helper);
                }
              }
            } catch (EnforcerRuleException e) {
              // i can throw an exception
              // because failfast will be
              // false if fail is false.
              if (failFast) {
                throw new MojoExecutionException(currentRule + " failed with message:\n" + e.getMessage(), e);
              } else {
                list.add("Rule " + i + ": " + currentRule + " failed with message:\n" + e.getMessage());
                log.debug("Adding failure due to exception", e);
              }
              log.error(e.getLongMessage());
            }
          }
        }

        // if we found anything
        if (!list.isEmpty()) {
          for (String failure : list) {
            log.warn(failure);
          }
          if (fail) {
            throw new MojoExecutionException(
                "Some Enforcer rules have failed. Look above for specific messages explaining why the rule failed.");
          }
        }
      } else {
        throw new MojoExecutionException(
            "No rules are configured. Use the skip flag if you want to disable execution.");
      }
    } else {
      log.info("Skipping Rule Enforcement.");
    }
  }
}
