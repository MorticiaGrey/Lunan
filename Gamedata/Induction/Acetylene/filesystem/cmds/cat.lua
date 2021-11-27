if args[1] == nil then
    print("usage: cat [FILE]")
    return
end

local path = lunan.getSelectedPath() .. args[1]
local contents = lunan.readFile(path, true)

if contents == nil then
    print(args[1] .. ": no such file or directory")
    return
end

for i = 1, tableLength(contents), 1 do
    print(contents[i])
end
