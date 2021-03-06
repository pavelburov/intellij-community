package com.intellij.structuralsearch.plugin.replace;

import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.structuralsearch.MatchOptions;
import com.intellij.structuralsearch.ReplacementVariableDefinition;
import gnu.trove.THashMap;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Maxim.Mossienko
 * Date: Mar 5, 2004
 * Time: 7:51:38 PM
 */
public class ReplaceOptions implements JDOMExternalizable, Cloneable, UserDataHolder {
  private Map<String, ReplacementVariableDefinition> variableDefs;
  private String replacement = "";
  private boolean toShortenFQN;
  private boolean myToReformatAccordingToStyle;
  private MatchOptions matchOptions = new MatchOptions();

  @NonNls private static final String REFORMAT_ATTR_NAME = "reformatAccordingToStyle";
  @NonNls private static final String REPLACEMENT_ATTR_NAME = "replacement";
  @NonNls private static final String SHORTEN_FQN_ATTR_NAME = "shortenFQN";

  private THashMap myUserMap = null;
  @NonNls private static final String VARIABLE_DEFINITION_TAG_NAME = "variableDefinition";

  public String getReplacement() {
    return replacement;
  }

  public void setReplacement(String replacement) {
    this.replacement = replacement;
  }

  public boolean isToShortenFQN() {
    return toShortenFQN;
  }

  public void setToShortenFQN(boolean shortedFQN) {
    this.toShortenFQN = shortedFQN;
  }

  public boolean isToReformatAccordingToStyle() {
    return myToReformatAccordingToStyle;
  }

  public MatchOptions getMatchOptions() {
    return matchOptions;
  }

  public void setMatchOptions(MatchOptions matchOptions) {
    this.matchOptions = matchOptions;
  }

  public void setToReformatAccordingToStyle(boolean reformatAccordingToStyle) {
    myToReformatAccordingToStyle = reformatAccordingToStyle;
  }

  public void readExternal(Element element) {
    matchOptions.readExternal(element);

    Attribute attribute = element.getAttribute(REFORMAT_ATTR_NAME);
    try {
      myToReformatAccordingToStyle = attribute.getBooleanValue();
    } catch(DataConversionException ex) {
    }

    attribute = element.getAttribute(SHORTEN_FQN_ATTR_NAME);
    try {
      toShortenFQN = attribute.getBooleanValue();
    } catch(DataConversionException ex) {}
    
    replacement = element.getAttributeValue(REPLACEMENT_ATTR_NAME);

    List<Element> elements = element.getChildren(VARIABLE_DEFINITION_TAG_NAME);

    if (elements!=null && elements.size() > 0) {
      for (final Element element1 : elements) {
        final ReplacementVariableDefinition variableDefinition = new ReplacementVariableDefinition();
        variableDefinition.readExternal(element1);
        addVariableDefinition(variableDefinition);
      }
    }
  }

  public void writeExternal(Element element) {
    matchOptions.writeExternal(element);

    element.setAttribute(REFORMAT_ATTR_NAME,String.valueOf(myToReformatAccordingToStyle));
    element.setAttribute(SHORTEN_FQN_ATTR_NAME,String.valueOf(toShortenFQN));
    element.setAttribute(REPLACEMENT_ATTR_NAME,replacement);

    if (variableDefs!=null) {
      for (final ReplacementVariableDefinition variableDefinition : variableDefs.values()) {
        final Element infoElement = new Element(VARIABLE_DEFINITION_TAG_NAME);
        element.addContent(infoElement);
        variableDefinition.writeExternal(infoElement);
      }
    }
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ReplaceOptions)) return false;

    final ReplaceOptions replaceOptions = (ReplaceOptions)o;

    if (myToReformatAccordingToStyle != replaceOptions.myToReformatAccordingToStyle) return false;
    if (toShortenFQN != replaceOptions.toShortenFQN) return false;
    if (matchOptions != null ? !matchOptions.equals(replaceOptions.matchOptions) : replaceOptions.matchOptions != null) return false;
    if (replacement != null ? !replacement.equals(replaceOptions.replacement) : replaceOptions.replacement != null) return false;
    if (variableDefs != null ? !variableDefs.equals(replaceOptions.variableDefs) : replaceOptions.variableDefs != null) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result;
    result = (replacement != null ? replacement.hashCode() : 0);
    result = 29 * result + (toShortenFQN ? 1 : 0);
    result = 29 * result + (myToReformatAccordingToStyle ? 1 : 0);
    result = 29 * result + (matchOptions != null ? matchOptions.hashCode() : 0);
    result = 29 * result + (variableDefs != null ? variableDefs.hashCode() : 0);
    return result;
  }

  public ReplaceOptions clone() {
    try {
      ReplaceOptions replaceOptions = (ReplaceOptions) super.clone();
      replaceOptions.matchOptions = matchOptions.clone();
      return replaceOptions;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      return null;
    }
  }

  public <T> T getUserData(@NotNull Key<T> key) {
    if (myUserMap==null) return null;
    return (T)myUserMap.get(key);
  }

  public <T> void putUserData(@NotNull Key<T> key, T value) {
    if (myUserMap==null) myUserMap = new THashMap(1);
    myUserMap.put(key,value);
  }

  public ReplacementVariableDefinition getVariableDefinition(String name) {
    return variableDefs != null ? variableDefs.get(name): null;
  }

  public void addVariableDefinition(ReplacementVariableDefinition definition) {
    if (variableDefs==null) {
      variableDefs = new LinkedHashMap<String, ReplacementVariableDefinition>();
    }
    variableDefs.put( definition.getName(), definition );
  }

  public Collection<ReplacementVariableDefinition> getReplacementVariableDefinitions() {
    return variableDefs != null ? variableDefs.values() : Collections.<ReplacementVariableDefinition>emptyList();
  }

  public void clearVariableDefinitions() {
    variableDefs = null;
  }
}
