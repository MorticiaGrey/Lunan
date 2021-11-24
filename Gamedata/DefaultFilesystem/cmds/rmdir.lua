if args[1] == nil then
    print("usage: rmdir [FILE]")
    return
end
local path = lunan.getSelectedPath() .. args[1]
if not lunan.removeChild(path) then
    print(args[1] .. ": no such file or directory")
end