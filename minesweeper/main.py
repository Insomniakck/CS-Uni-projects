#211315262 Sofia Tchernikov

import tkinter
from tkinter import Tk, Button, messagebox,Label
import random

# Class to represent a single cell in the minesweeper grid
class Cell:
    def __init__(self, neighbors, row=0, column=0):
        self.isRevealed = False
        self.hasMine = False
        self.isFlagged = False
        self.adjMines = 0
        self.neighbors = neighbors

    def addAdjMine(self):
        self.adjMines += 1

    def changeFlag(self, event=None):
        self.isFlagged = not self.isFlagged

    def setMine(self):
        self.hasMine = True

# Class to represent the board for the Minesweeper game
class Board:
    def __init__(self, size, root, numOfMines):
        self.__size = int(size)
        self.__numOfCells = size ** 2 - numOfMines
        self.__numOfMines = int(numOfMines)
        self.__cells = []            # List to hold all the cell objects
        self.__buttons = []          # List to hold all the button widgets
        self.colors = ["MediumBlue","LimeGreen","Red","DarkBlue","Maroon","Teal","Black","Gray"]
        self.__label = Label(root, text="Bombs left: " +str(self.__numOfMines))
        self.__label.grid(row = size,columnspan=1000)
        # Generate random positions for mines
        mines = random.sample(range(0, size ** 2), numOfMines)


        for i in range(size):
            cellsRow = []
            buttonsRow = []
            for j in range(size):
                # Get neighboring cells for the current cell
                neighbors = self.getCellNeighbors(i, j, size)
                cell = Cell(neighbors, i, j)
                cellsRow.append(cell)

                if i * size + j in mines:
                    cell.setMine()
                else:
                    for x in neighbors:
                        if x in mines:
                            cell.addAdjMine()

                button = Button(root, width=2, height=1, bg="dark gray")
                button.grid(row=i, column=j)
                button.bind("<Button-1>", self.revealSpace)
                button.bind("<Button-3>", self.toggleFlag)
                buttonsRow.append(button)

            self.__cells.append(cellsRow)
            self.__buttons.append(buttonsRow)

    # Update the bomb counter label
    def updateBombLabel(self,num):
        self.__numOfMines+=num
        self.__label.config(text="Bombs left: " + str(self.__numOfMines))

    # Toggle the flag status of the cell when right-clicked
    def toggleFlag(self, event=None):

        button = event.widget
        row = button.grid_info()['row']
        column = button.grid_info()['column']
        cell = self.__cells[row][column]
        if cell.isRevealed:
            return
        cell.changeFlag()
        if cell.isFlagged:
            button.config(bg="yellow", text='|>')
            self.updateBombLabel(-1)
        else:
            button.config(bg="dark gray", text=' ')
            self.updateBombLabel(1)

    # Reveal the cell when left-clicked
    def revealSpace(self, event=None):
        button = event.widget
        row = button.grid_info()['row']
        column = button.grid_info()['column']
        cell = self.__cells[row][column]

        if cell.isFlagged or cell.isRevealed:
            return
        if cell.hasMine:
            button.config(bg="red", state=tkinter.DISABLED, text="B")
            messagebox.showinfo("Game Over", "You hit a bomb!")
            quit()
        else:
            if cell.adjMines == 0:
                self.revealRecursive(row, column)
            else:
                cell.isRevealed = True
                self.__numOfCells -= 1
                button.config(bg="white",disabledforeground=self.colors[cell.adjMines-1],
                              state=tkinter.DISABLED, text=cell.adjMines)
            if self.__numOfCells == 0:
                messagebox.showinfo("Congratulations!", "You won the game!")
                quit()


    # Recursively reveal neighboring cells if they are empty (adjMines == 0)
    def revealRecursive(self, row, column):
        cell = self.__cells[row][column]
        if cell.isRevealed:
            return
        cell.isRevealed = True
        self.__numOfCells -= 1
        if cell.adjMines != 0:
            self.__buttons[row][column].config(bg="white",disabledforeground=self.colors[cell.adjMines-1],
                                               state=tkinter.DISABLED,text=cell.adjMines)
        else:
            self.__buttons[row][column].config(bg="white", state=tkinter.DISABLED)
            for x in cell.neighbors:
                self.revealRecursive(x // self.__size, x % self.__size)



    def getCellNeighbors(self, row, column, size):
        neighbors = [(row - 1) * size + column - 1, (row - 1) * size + column, (row - 1) * size + column + 1,
                     row * size + column - 1,
                     row * size + column + 1, (row + 1) * size + column - 1, (row + 1) * size + column,
                     (row + 1) * size + column + 1]
        if size == 1:
            neighbors = []
        elif row == 0 and column == 0:
            del neighbors[5]
            del neighbors[3]
            del neighbors[0:3]
        elif row == 0 and column == size - 1:
            del neighbors[7]
            del neighbors[4]
            del neighbors[0:3]
        elif row == size - 1 and column == 0:
            del neighbors[5:8]
            del neighbors[3]
            del neighbors[0]
        elif row == size - 1 and column == size - 1:
            del neighbors[5:8]
            del neighbors[4]
            del neighbors[2]
        elif column == 0:
            del neighbors[5]
            del neighbors[3]
            del neighbors[0]
        elif column == size - 1:
            del neighbors[7]
            del neighbors[4]
            del neighbors[2]
        elif row == 0:
            del neighbors[0:3]
        elif row == size - 1:
            del neighbors[5:8]

        return neighbors

# Main Minesweeper game class
class Minesweeper:
    def __init__(self, root, boardSize, numOfMine):
        self.__root = root
        self.__board = Board(boardSize, root, numOfMine)

    def run(self):
        self.__root.mainloop()


if __name__ == "__main__":
    try:
        s = int(input("Enter the size of the board: "))
        b = int(input("Enter the number of the bombs: "))
    except ValueError as e:
        print("Invalid input. Please select a number.")
        quit()
    if s<1 or b<0:
        print("Invalid number. Please choose a different number.")
        quit()

    root = Tk()
    game = Minesweeper(root, boardSize=s, numOfMine=b)
    game.run()
