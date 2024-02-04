/*
 * Copyright (C) 2023 The Insmont Open Source Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.insmont.model;

import lombok.Value;

/**
 * @author chuhelan
 * @version 1.0
 * @date Monday 16 October 2023 8:51 AM
 * @package: org.insmont.model
 * @Desc:
 */

/**
 * Represents an immutable code-message pair.
 */

public record CodeMessage(int code, String message) {
}