local printVal = ""
local length = tableLength(args)

for i = 4, length, 1 do
    printVal = printVal .. " " .. args[i]
end

print(printVal)
