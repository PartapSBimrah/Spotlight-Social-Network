/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphAnimations.exception;

/**
 * Created by Tuyen Nguyen on 2/12/17.
 */

public class InvalidNumberOfPulseException extends Exception {

  @Override public String getMessage() {
    return "The number of pulse must be between 2 and 6";
  }
}
