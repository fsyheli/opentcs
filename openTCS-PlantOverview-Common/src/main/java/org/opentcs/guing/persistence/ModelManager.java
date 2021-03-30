/*
 * openTCS copyright information:
 * Copyright (c) 2014 Fraunhofer IML
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.persistence;

import org.opentcs.access.KernelServicePortal;
import org.opentcs.guing.model.SystemModel;

/**
 * Manages (loads, persists and keeps) the driving course model.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public interface ModelManager {

  /**
   * Returns the current system model.
   *
   * @return The current system model.
   */
  SystemModel getModel();

  /**
   * Creates a new, empty system model.
   */
  void createEmptyModel();

  /**
   * Persists the given system model in to a file.
   *
   * @param chooseName Whether a dialog to choose a name shall be shown.
   * @return Whether the model was actually saved.
   */
  boolean persistModel(boolean chooseName);
  
  /**
   * Creates figures and process adapters for all model components in the current system model.
   */
  void restoreModel();

  /**
   * Loads all model objects from the kernel and creates the corresponding
   * figures.
   *
   * @param portal The kernel client portal.
   */
  void restoreModel(KernelServicePortal portal);
}