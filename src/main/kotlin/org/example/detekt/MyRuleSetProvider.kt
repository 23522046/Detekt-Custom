package org.example.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.example.detekt.smells.*

class MyRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "MyRuleSet"

    override fun instance(config: Config): RuleSet {
        return RuleSet(
            ruleSetId,
            listOf(
                TooManyFuns(config),
                CyclomaticComplexityM(config),
                BrainClass(config),
                BrainMethod(config),
                GodClass(config),
                DataClass(config),
                FeatureEnvy(config)
            ),
        )
    }
}
